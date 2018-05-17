package com.nguyen.springbootdocker.rabbitmq.provider;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.nguyen.foun.utils.ByteUtils;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
@Slf4j
public class BlockingQueueConsumer extends DefaultConsumer {
    private final BlockingQueue<Delivery> queue = new ArrayBlockingQueue<>(1024);
    private final String queueName;
    private final boolean autoAck;
    private final RabbitMessageHandler messageHandler;

    /**
     * Constructs a new instance and records its association to the passed-in channel.
     *
     * @param channel the channel to which this consumer is attached
     * @param queueName
     * @param autoAck
     * @param messageHandler
     */
    public BlockingQueueConsumer(Channel channel,
                                 String queueName,
                                 boolean autoAck,
                                 RabbitMessageHandler messageHandler) {
        super(channel);
        this.queueName = queueName;
        this.autoAck = autoAck;
        this.messageHandler = messageHandler;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            queue.put(new Delivery(envelope, properties, body));
        } catch (InterruptedException e) {
            log.error("rabbit consumer handle delivery queue: %s error %s", queueName, e);
        }
    }

    public void nextDelivery() throws InterruptedException, IOException {
        Delivery delivery = queue.take();
        String type = delivery.getProperties().getContentType();
        byte[] body = delivery.getBody();
        if (log.isDebugEnabled()) {
            log.debug("rabbit consumer queue: %s type: %s, body: %s", queueName, type, ByteUtils.string(body));
        }
        try {
            messageHandler.handle(type, body);
            super.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (Exception e) {
            if(autoAck) {
                super.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                log.error("rabbit consumer queue: %s type: %s body: %s error %s", queueName, type, ByteUtils.string(body), e);
            }
        }
    }
}
