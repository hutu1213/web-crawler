package com.example.webcrawler.queue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class RabbitMQService implements AutoCloseable{
    private final Connection connection;
    private final Channel channel;

    private static final String DLX_SUFFIX = "_DLQ"; // Suffix for Dead Letter Queue names

    public RabbitMQService() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("admin");
        factory.setPassword("admin");
        connection = factory.newConnection();
        channel = connection.createChannel();

        // Declare DLQs and main queues with DLX (Dead Letter Exchange)
        declareQueueWithDLX(QueueEnum.Name.FRONTIER_QUEUE);
        declareQueueWithDLX(QueueEnum.Name.PARSING_QUEUE);
    }

    private void declareQueueWithDLX(QueueEnum.Name queueName) throws Exception {
        String mainQueue = queueName.getQueueName();
        String dlqQueue = mainQueue + DLX_SUFFIX;  // DLQ name with a suffix

        // Declare DLQ
        channel.queueDeclare(dlqQueue, true, false, false, null);

        // Declare the main queue with DLX configuration
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", ""); // Using the default exchange
        arguments.put("x-dead-letter-routing-key", dlqQueue); // DLQ routing key

        // Declare main queue with dead letter configuration
        channel.queueDeclare(mainQueue, true, false, false, arguments);
    }

    public void sendMessageToQueue(QueueEnum.Name queue, String message) throws Exception {
        channel.basicPublish("", queue.getQueueName(), null, message.getBytes(StandardCharsets.UTF_8));
    }

    public void receiveMessages(QueueEnum.Name queue, DeliverCallback deliverCallback) throws Exception {
        channel.basicConsume(queue.getQueueName(), true, deliverCallback, consumerTag -> {});
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
        System.out.println("RabbitMQ connection and channel closed.");
    }
}
