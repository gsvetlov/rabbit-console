package ru.svetlov.rabbit.console.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ConsoleReader implements Runnable {
    private final Consumer<String> consumer;

    public ConsoleReader(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void run() {
        try (InputStreamReader in = new InputStreamReader(System.in);
             BufferedReader reader = new BufferedReader(in)) {

            while (!Thread.currentThread().isInterrupted()) {
                consumer.accept(reader.readLine());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
