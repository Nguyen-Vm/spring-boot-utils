package com.nguyen.rabbitmq.provider;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author RWM
 * @date 2018/6/21
 */
public final class RabbitConnection {

    private RabbitConnection() {}

    public static synchronized Connection create(String mqUri) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(mqUri);
            return factory.newConnection();
        } catch (Exception e) {
            throw new RuntimeException(mqUri + ", create rabbitmq connection error: ", e);
        }
    }
}
