package org.example.model;

public class TopicResponse {

    private String name;
    private int subscribers;

    public TopicResponse(String name, int subscribers) {
        this.name = name;
        this.subscribers = subscribers;
    }

    public String getName() {
        return name;
    }

    public int getSubscribers() {
        return subscribers;
    }

}
