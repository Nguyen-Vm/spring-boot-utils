package com.nguyen.springbootdocker.rabbitmq.provider;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public final class RabbitConnectionFactory {

    // rabbit 地址 AMQP://{user:pwd}@{host:port}/{vHost}
    //rabbit.default.uri=AMQP://rwming:rwming@localhost:5672/{virtual host}
	public static synchronized Connection create(String mqUri) {
		log.info("rabbit mq uri: %s", mqUri);
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri(mqUri);
			factory.setAutomaticRecoveryEnabled(true);
			factory.setRequestedHeartbeat(ConnectionFactory.DEFAULT_HEARTBEAT/2);
			factory.setConnectionTimeout(ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT/10);
			factory.setNetworkRecoveryInterval(ConnectionFactory.DEFAULT_SHUTDOWN_TIMEOUT/2);
			return factory.newConnection();
		} catch (Exception e) {
			throw new RuntimeException("create connection provider uri: " + mqUri, e);
		}
	}
}
