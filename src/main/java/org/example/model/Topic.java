package org.example.model;


import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Topic {

    private final String name;
    // Thread-safe set of subscribers
    private final Set<Subscriber> subscribers = ConcurrentHashMap.newKeySet();

    public Topic(String name) {
        this.name = name;
    }


    public String getName() { return name; }
    public int getSubscriberCount() { return subscribers.size(); }

    public void addSubscriber(WebSocketSession session, int queueSize) {
        Subscriber subscriber = new Subscriber(session, queueSize);
        subscribers.add(subscriber);
    }

    public void removeSubscriber(WebSocketSession session) {
        subscribers.removeIf(sub -> sub.getSession().equals(session));
    }

    public void publishMessage(String message) {
        for (Subscriber sub : subscribers) {
            boolean ok = sub.offerMessage(message);
            if (!ok) {
                // Backpressure policy: disconnect slow subscriber
                try { sub.getSession().close(); } catch (Exception ignored) {}
                subscribers.remove(sub);
            }
        }
    }

    public void closeAllSubscribers() {
        for (Subscriber sub : subscribers) {
            sub.stop();
        }
        subscribers.clear();
    }
}
