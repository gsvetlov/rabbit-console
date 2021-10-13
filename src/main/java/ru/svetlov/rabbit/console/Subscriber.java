package ru.svetlov.rabbit.console;

import com.rabbitmq.client.*;
import ru.svetlov.rabbit.console.util.ConsoleReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Subscriber {
    private static final String EXCHANGE_NAME = "it-channel";
    private Thread readerThread;

    public static void main(String[] args) throws Exception {
        new Subscriber().subscribe();
    }

    public void subscribe() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        readerThread = new Thread(new ConsoleReader(s -> {
            if (s.length() < 2) return;
            if (s.equals("exit")) {
                readerThread.interrupt();
            }
            try {
                if (s.startsWith("+")) {
                    channel.queueBind(queueName, EXCHANGE_NAME, s.substring(1));
                }
                if (s.startsWith("-")) {
                    channel.queueUnbind(queueName, EXCHANGE_NAME, s.substring(1)); // TODO: можно вести лист каналов и удалять\добавлять их по маске *.*
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        readerThread.start();

        System.out.println(" [*] Waiting for messages. Type 'exit' to quit, '+{theme.topic}' to subscribe, '-{theme.topic}' to unsubscribe");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

        readerThread.join();
        channel.close();
        connection.close();
    }
}
