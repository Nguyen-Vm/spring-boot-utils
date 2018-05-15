package com.nguyen.springbootdocker.provider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * @author RWM
 * @date 2018/3/21
 * @description:
 */
public class RabbitProducer {
    private static final String QUEUE_NAME = "hello";
    private static final String TASK_QUEUE_NAME = "task_queue";


    private static final String EXCHANGE_NAME = "exchange";
    private static final String FANOUT = "fanout";
    private static final String DURABLE_QUEUE_NAME = "durable_queue";
    private static final String ROUTING_KEY = "";
    private static final String MESSAGE = "Hello, it's me !";
    private static final String URI = "AMQP://ruochu:Ruochu123@192.168.29.130:5672/message";

    public static void main(String[] args) throws Exception {

        for (int i = 0 ; i < 20 ;i++){
            helloWorldProducer(URI);

//            workQueuesProducer(URI);
        }

        for (int i = 0 ; i < 20 ;i++){
//            helloWorldProducer(URI);

//            workQueuesProducer(URI);
        }
//        publish(URI);

//        producer1(URI);
    }



    private static void producer(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        channel.basicPublish(EXCHANGE_NAME, "", null, MESSAGE.getBytes());
        System.out.println(" [producer] Sent '" + MESSAGE + "'");
    }

    private static void publish(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(FANOUT, "fanout");
        channel.basicPublish(FANOUT, "",null, MESSAGE.getBytes());
        System.out.println(" [publish] Sent '" + MESSAGE + "'");
        channel.close();
        connection.close();
    }

    private static void workQueuesProducer(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, MESSAGE.getBytes());
        System.out.println(" [workQueuesProducer] Sent '" + MESSAGE + "'");
        Thread.sleep(1000);
        channel.close();
        connection.close();
    }

    private static void helloWorldProducer(String uri) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, MESSAGE.getBytes());
        System.out.println(" [helloWorldProducer] Sent '" + MESSAGE + "'");
    }

}
