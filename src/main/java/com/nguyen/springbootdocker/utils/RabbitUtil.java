package com.nguyen.springbootdocker.utils;

import com.alibaba.fastjson.JSON;
import com.nguyen.springbootdocker.rabbitmq.provider.RabbitConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
@Slf4j
public class RabbitUtil {

    private final Connection connection;
    final Channel senderChannel;

    public RabbitUtil(String mqUri) throws IOException {
        this.connection = RabbitConnectionFactory.create(mqUri);
        this.senderChannel = connection.createChannel();
    }

    /** 生产者 **/
    public void producer(String exchange, String routingKey, Object object){
        if (log.isDebugEnabled()){
            log.debug("rabbit produce, exchange: {}, msg: {}", exchange, JSON.toJSONString(object));
        }
        try {
            senderChannel.exchangeDeclare(exchange,"direct", true);
            senderChannel.basicPublish(exchange, routingKey, null, JSON.toJSONBytes(object));
        } catch (IOException e) {
            log.error("provider produce direct msg error.....", e);
        }
    }

    /** 消费者 **/
    public void consumer(String exchange, String routingKey){
        Executors.newSingleThreadExecutor().submit(() -> {
            if (log.isInfoEnabled()){
                log.info("rabbit consume, exchange:{}", exchange);
            }
            try {
                senderChannel.exchangeDeclare(exchange, "direct", true);
                String queueName = senderChannel.queueDeclare().getQueue();
                senderChannel.queueBind(queueName, exchange, routingKey);
                Consumer consumer = new DefaultConsumer(senderChannel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [X] Received '" + message + "'");
                    }
                };
                senderChannel.basicConsume(queueName, true, consumer);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    /** 发布者 **/
    public void publish(String exchange, Object object){
        if (log.isDebugEnabled()){
            log.debug("rabbit publish, exchange: {}, msg: {}", exchange, JSON.toJSONString(object));
        }
        try {
            senderChannel.exchangeDeclare(exchange,"fanout",true);
            senderChannel.basicPublish(exchange,"", null, JSON.toJSONBytes(object));
        } catch (IOException e) {
            log.error("provider publish direct msg error.....", e);
        }
    }

}
