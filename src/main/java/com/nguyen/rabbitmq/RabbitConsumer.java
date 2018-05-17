package com.nguyen.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
@Slf4j
public class RabbitConsumer {
    private static final String QUEUE_NAME = "hello";
    private static final String TASK_QUEUE_NAME = "task_queue";


    private static final String EXCHANGE_NAME = "exchange";
    private static final String FANOUT = "fanout";
    private static final String DURABLE_QUEUE_NAME = "durable_queue";
    private static final String ROUTING_KEY = "";
    private static final String URI = "AMQP://ruochu:Ruochu123@192.168.29.130:5672/message";

    /**
     * autoAck-是否自动告知
     * */
    public static void main(String[] args) throws Exception {

        helloWorldConsumer(URI);

//        workQueuesConsumer(URI);
//        workQueuesConsumer(URI);
//        workQueuesConsumer(URI);
//        workQueuesConsumer(URI);
//        workQueuesConsumer(URI);
//        workQueuesConsumer(URI);

//        subscribe(URI);

//        consumer(URI);
    }

    private static void consumer(String url) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(url);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [consumer] Waiting for messages. To exit press CTRL+C");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("exchange: " + envelope.getExchange());
                System.out.println("routingKey: " + envelope.getRoutingKey());
                System.out.println("deliveryTag: " + envelope.getDeliveryTag());
                String message = new String(body, "UTF-8");
                System.out.println(" [consumer] Received '" + message + "'");
                System.out.println("---------------------------------------------------------");
            }
        });
    }

    private static void subscribe(String url) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(url);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(FANOUT, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, FANOUT, "");

        System.out.println(" [subscribe] Waiting for messages. To exit press CTRL+C");

        channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("exchange: " + envelope.getExchange());
                System.out.println("routingKey: " + envelope.getRoutingKey());
                System.out.println("deliveryTag: " + envelope.getDeliveryTag());
                String message = new String(body, "UTF-8");
                System.out.println(" [subscribe] Received '" + message + "'");
                System.out.println("---------------------------------------------------------");
            }
        });
    }

    private static void workQueuesConsumer(String url) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(url);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [workQueuesConsumer] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        channel.basicConsume(TASK_QUEUE_NAME, false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("exchange: " + envelope.getExchange());
                System.out.println("routingKey: " + envelope.getRoutingKey());
                System.out.println("deliveryTag: " + envelope.getDeliveryTag());
                String message = new String(body, "UTF-8");
                System.out.println(" [workQueuesConsumer] Received '" + message + "'");
                System.out.println("---------------------------------------------------------");
                //手动ack-手动告知
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

    private static void helloWorldConsumer(String url) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(url);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        System.out.println(" [hello world] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(2);

        channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("exchange: " + envelope.getExchange());
                System.out.println("routingKey: " + envelope.getRoutingKey());
                System.out.println("deliveryTag: " + envelope.getDeliveryTag());
                String message = new String(body, "UTF-8");
                System.out.println(" [helloWorldConsumer] Received '" + message + "'");
                System.out.println("---------------------------------------------------------");
                //手动ack-手动告知
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }
}
