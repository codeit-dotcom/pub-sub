package org.example.service;

import org.example.model.Topic;
import org.example.model.TopicResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class PubSubService {

    private static final int SUBSCRIBER_QUEUE_SIZE = 10; // Backpressure limit
    private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    // --- Topic management ---
    public boolean createTopic(String topicName) {
        return topics.putIfAbsent(topicName, new Topic(topicName)) == null;
    }

    public boolean deleteTopic(String topicName) {
        Topic topic = topics.remove(topicName);
        if (topic != null) {
            topic.closeAllSubscribers();
            return true;
        }
        return false;
    }

    public List<TopicResponse> listTopics() {
        return topics.values().stream()
                .map(t -> new TopicResponse(t.getName(), t.getSubscriberCount()))
                .toList();
    }

    // --- Subscribe / Unsubscribe ---
    public boolean subscribe(String topicName, WebSocketSession session) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.addSubscriber(session, SUBSCRIBER_QUEUE_SIZE);
            return true;
        }
        return false;
    }

    public boolean unsubscribe(String topicName, WebSocketSession session) {
        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.removeSubscriber(session);
            return true;
        }
        return false;
    }

    // --- Publish ---
    public void publish(String topicName, String message) {
        if (!running) return;

        Topic topic = topics.get(topicName);
        if (topic != null) {
            topic.publishMessage(message);
        }
    }

    // --- Graceful shutdown ---
    //@PreDestroy
    public void shutdown() {
        running = false;
        for (Topic topic : topics.values()) {
            topic.closeAllSubscribers();
        }
    }

    public Map<String, Object> getHealthStats() {
        String uptime = Instant.now().toString();
        int topicCount = topics.size();
        int subscriberCount =topics.values().stream()
                .mapToInt(Topic::getSubscriberCount) // call method from Topic
                .sum();

        return Map.of(
                "uptime_sec", uptime,
                "topics", topicCount,
                "subscribers", subscriberCount
        );
    }

    public Map<String, Map<String, Object>> getStats() {
        Map<String, Map<String, Object>> stats = new HashMap<>();
        for (Topic topic : topics.values()) {
            Map<String, Object> topicStats = new HashMap<>();
            topicStats.put("subscribers", topic.getSubscriberCount());
            stats.put(topic.getName(), topicStats);
        }
        return stats;
    }

}