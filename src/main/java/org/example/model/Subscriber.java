package org.example.model;

import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class Subscriber {

    @Getter
    private final WebSocketSession session;
    private final BlockingQueue<String> queue; // bounded for backpressure
    private final Thread senderThread;

    public Subscriber(WebSocketSession session, int queueSize) {
        this.session = session;
        this.queue = new ArrayBlockingQueue<>(queueSize);

        senderThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && session.isOpen()) {
                    String msg = queue.take();
                    session.sendMessage(new TextMessage(msg));
                }
            } catch (Exception ignored) {
            }
        });
        senderThread.start();
    }

    public boolean offerMessage(String msg) {
        return queue.offer(msg); // returns false if full
    }

    public void stop() {
        senderThread.interrupt();
        try {
            session.close();
        } catch (Exception ignored) {
        }
    }
}

