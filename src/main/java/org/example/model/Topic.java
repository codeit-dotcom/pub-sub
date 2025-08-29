package org.example.model;

import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Topic {

    private final String name;
    private final Set<Session> subscribers = ConcurrentHashMap.newKeySet();

    public Topic(String name) {
        this.name = name;
    }

    public void addSubscriber(Session session) {
        subscribers.add(session);
    }

    public void removeSubscriber(Session session) {
        subscribers.remove(session);
    }

    public Set<Session> getSubscribers() {
        return subscribers;
    }

}
