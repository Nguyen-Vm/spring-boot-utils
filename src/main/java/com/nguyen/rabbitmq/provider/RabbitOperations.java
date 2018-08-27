package com.nguyen.rabbitmq.provider;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.nguyen.helper.ThreadFactoryHelper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.lang3.StringUtils;
import org.nguyen.foun.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

/**
 * @author RWM
 * @date 2018/6/21
 */
public class RabbitOperations {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitOperations.class);

    private final ConcurrentMap<Class<?>, String> classNameMap = Maps.newConcurrentMap();

    private final Connection connection;
    public final Channel channel;

    public RabbitOperations(String mqUri) throws IOException {
        this.connection = RabbitConnection.create(mqUri);
        this.channel = connection.createChannel();
    }

    /**
     * 消息生产者
     *
     * @param topic
     * @param message
     */
    public void producer(Enum topic, Object message) {
        try {
            AMQP.BasicProperties props = propertyOf(message, true);
            channel.exchangeDeclare(topic.name(), "direct", true);
            channel.basicPublish(topic.name(), topic.name(), props, JSON.toJSONBytes(message));
        } catch (Exception e) {
            throw new RuntimeException("rabbitmq send direct msg error: ", e);
        }
    }

    /**
     * 消息消费者
     *
     * @param topic
     * @param autoAck
     * @param messageHandler
     */
    public void consumer(Enum topic, boolean autoAck, RabbitMessageHandler messageHandler) {
        Executors.newSingleThreadExecutor(ThreadFactoryHelper.threadFactoryOf(topic.name())).submit(() -> {
            while (true) {
                try {
                    BlockingQueueConsumer consumer = consumer(topic, "direct", topic.name(), autoAck, messageHandler);
                    if (null == consumer) {
                        try {
                            Thread.sleep(ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
                        } catch (InterruptedException ie) {
                            continue;
                        }
                        continue;
                    }
                    // 消费出现异常
                    if (null != consumingLoop(consumer)) {
                        closeConsumer(topic, consumer);
                        LOG.error("rabbit consumer topic: {} about {}s to retry", topic, ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
                        Thread.sleep(ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
                    }
                } catch (InterruptedException ie) {
                    continue;
                } catch (Exception e) {
                    LOG.error("rabbit consumer topic: {} error %s", topic, e);
                    try {
                        Thread.sleep(ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT);
                    } catch (InterruptedException ie) {
                        continue;
                    }
                }
            }
        });
    }

    private void closeConsumer(Enum topic, BlockingQueueConsumer consumer) {
        if (null != consumer && null != consumer.getChannel()) {
            try {
                consumer.getChannel().basicCancel(consumer.getConsumerTag());
            } catch (Exception e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("rabbit topic: %s consumer close error %s", topic, e);
                }
            }
        }
        terminateConsumingLoop();
    }

    private void terminateConsumingLoop() {
        consumingLoop = false;
    }

    private AMQP.BasicProperties propertyOf(Object message, boolean durable) {
        return new AMQP.BasicProperties
                .Builder()
                .contentType(getContentType(message))
                .deliveryMode(durable ? 2 : 1)
                .timestamp(DateUtils.now())
                .build();
    }

    private String getContentType(Object message) {
        Class<?> clazz = message.getClass();
        String className = classNameMap.get(clazz);
        if (StringUtils.isBlank(className)) {
            className = clazz.getSimpleName();
            classNameMap.put(clazz, className);
        }
        return className;
    }

    private volatile boolean consumingLoop = true;

    private Exception consumingLoop(BlockingQueueConsumer consumer) throws IOException {
        try {
            while (consumingLoop) {
                try {
                    consumer.nextDelivery();
                } catch (InterruptedException ie) {
                    continue;
                }
            }
            return null;
        } catch (ShutdownSignalException sse) {
            return sse;
        }
    }

    private BlockingQueueConsumer consumer(Enum topic, String type, String queueName, boolean autoAck, RabbitMessageHandler messageHandler) {
        BlockingQueueConsumer consumer = null;
        try {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(topic.name(), type, true);
            boolean isFanout = isFanout(type);
            channel.queueDeclare(queueName, true, isFanout, isFanout, null);
            // 绑定队列到交换机
            channel.queueBind(queueName, topic.name(), isFanout ? StringUtils.EMPTY : topic.name());
            // 同一时刻服务器只会发一条消息给消费者;
            channel.basicQos(1);
            consumer = new BlockingQueueConsumer(channel, queueName, autoAck, messageHandler);
            channel.basicConsume(queueName, false, consumer);
        } catch (Exception e) {
            LOG.error("rabbit consume topic: {} type: {} error {}", topic, type, e);
            closeConsumer(topic, consumer);
        }
        return consumer;
    }

    private boolean isFanout(String exchange) {
        return "fanout".equals(exchange);
    }

    private class BlockingQueueConsumer extends DefaultConsumer {
        private final BlockingQueue<Delivery> queue = new ArrayBlockingQueue<>(1024);
        private final String queueName;
        private final boolean exceptionAutoAck;
        private final RabbitMessageHandler messageHandler;

        public BlockingQueueConsumer(Channel channel, String queueName, boolean exceptionAutoAck, RabbitMessageHandler messageHandler) {
            super(channel);
            this.queueName = queueName;
            this.exceptionAutoAck = exceptionAutoAck;
            this.messageHandler = messageHandler;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            try {
                queue.put(new Delivery(envelope, properties, body));
            } catch (InterruptedException e) {
                LOG.error("rabbit consumer handle delivery queue: {} error {}", queueName, e);
            }
        }

        public void nextDelivery() throws InterruptedException, IOException {
            Delivery delivery = queue.take();
            String contentType = delivery.getProperties().getContentType();
            byte[] body = delivery.getBody();
            try {
                messageHandler.handle(contentType, body);
                // 处理完消息之后，向服务器确认消息
                super.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                if (exceptionAutoAck) {
                    // 处理完消息之后，向服务器确认消息
                    super.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    LOG.error("rabbit consumer queue: {} type: {} body: {} error {}", queueName, contentType, new String(body, "UTF-8"), e);
                }
            }
        }


    }
}
