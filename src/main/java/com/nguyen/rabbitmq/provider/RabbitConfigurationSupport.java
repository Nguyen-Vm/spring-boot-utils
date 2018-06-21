package com.nguyen.rabbitmq.provider;


import org.springframework.context.annotation.Bean;

import java.io.IOException;

/**
 * @author RWM
 * @date 2018/6/21
 */
public abstract class RabbitConfigurationSupport {

    protected abstract String defaultMqUri();

    @Bean
    public RabbitOperations defaultRabbitOperations() throws IOException {
        return new RabbitOperations(defaultMqUri());
    }
}
