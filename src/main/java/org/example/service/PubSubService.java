package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class PubSubService {

    private final Map<String, Set<WebSocketSession>> topics = new ConcurrentHashMap<>();

    // Topic management
    public boolean createTopic(String topicName) {
        return topics.putIfAbsent(topicName, ConcurrentHashMap.newKeySet()) == null;
    }

    public boolean deleteTopic(String topicName) {
        return topics.remove(topicName) != null;
    }

    public Set<String> listTopics() {
        return topics.keySet();
    }

    // Subscribe
    public boolean subscribe(String topicName, WebSocketSession session) {
        Set<WebSocketSession> subscribers = topics.get(topicName);
        if (subscribers != null) {
            subscribers.add(session);
            return true;
        }
        return false;
    }

    public boolean unsubscribe(String topicName, WebSocketSession session) {
        Set<WebSocketSession> subscribers = topics.get(topicName);
        if (subscribers != null) {
            subscribers.remove(session);
            return true;
        }
        return false;
    }

    // Publish
    public void publish(String topicName, String message) {
        Set<WebSocketSession> subscribers = topics.get(topicName);
        if (subscribers != null) {
            for (WebSocketSession s : subscribers) {
                try {
                    if (s.isOpen()) {
                        s.sendMessage(new org.springframework.web.socket.TextMessage(message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
