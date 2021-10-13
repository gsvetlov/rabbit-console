package ru.svetlov.rabbit.console;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Publisher {
    private final String XCHNG_NAME = "it-channel";
    private final int MESSAGES_TO_SEND = 40;
    private final long SEND_INTERVAL = 2000;

    private static final Random random = new Random();
    private final ConnectionFactory factory;

    public Publisher() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    public void publish() throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(XCHNG_NAME, BuiltinExchangeType.TOPIC);
            for (int i = 0; i < MESSAGES_TO_SEND; i++) {
                ChannelMessage message = new ChannelMessage();
                channel.basicPublish(XCHNG_NAME, message.topic, null, message.contents.getBytes(StandardCharsets.UTF_8));
                System.out.println("sent: " + message);
                Thread.sleep(SEND_INTERVAL);
            }
        }
    }

    private static class ChannelMessage {
        private final String PREFIX = "it.";
        private final String[] topics = {"java", "c#", "python", "c++", "go", "erlang", "kotlin", "scala", "rust"};
        private final String lorem = "Lorem, ipsum dolor sit amet consectetur adipisicing elit. Quod nam cupiditate cum esse in ut explicabo quaerat dicta quidem modi. Nesciunt, nemo distinctio non tenetur facere officia eius. Temporibus dolorem aut quis inventore hic, ab excepturi quos totam, tempore commodi repellendus eaque omnis optio quo facere vel cumque soluta sed.";

        private final String topic;
        private final String contents;

        private ChannelMessage() {
            topic = PREFIX + topics[random.nextInt(topics.length)];
            contents = lorem.substring(0, random.nextInt(lorem.length() - 3) + 3);
        }

        @Override
        public String toString() {
            return "[" + topic + "]:" + contents;
        }
    }

    public static void main(String[] args) throws Exception {
        new Publisher().publish();
        System.out.println("#####\nDone.");
    }
}
