package com.nguyen.springbootdocker.rabbitmq.provider;

public interface RabbitMessageHandler {
    default byte[] handle(String contentType, byte[] body){
        return new byte[0];
    }
}