package com.nguyen.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.nguyen.dto.RabbitMessage;
import com.nguyen.rabbitmq.provider.RabbitMessageHandler;
import com.nguyen.rabbitmq.provider.RabbitOperations;

public class RabbitProducerTest {

    private static final String MQURI = "AMQP://rwming:rwming@localhost:5672/message";

    public static void main(String[] args) throws Exception {
        RabbitOperations rabbitOperations = new RabbitOperations(MQURI);

        rabbitOperations.consumer(RabbitTopic.DeveloperAction, false, new RabbitMessageHandler() {
            @Override
            public byte[] handle(String contentType, byte[] body) {
                if ("RabbitMessage".equals(contentType)) {
                    RabbitMessage request = JSON.parseObject(body, RabbitMessage.class);
                    System.out.println(request);
                }
                return null;
            }
        });

        RabbitMessage request = new RabbitMessage();
        request.name = "阮威敏";
        request.age = 23;
        request.phone = "17301747367";
        rabbitOperations.producer(RabbitTopic.DeveloperAction, request);

    }

}