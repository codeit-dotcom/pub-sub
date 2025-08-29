package org.example.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.example.service.PubSubService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final PubSubService pubSubService;

    public MyWebSocketHandler(PubSubService pubSubService) {
        this.pubSubService = pubSubService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received WS message from {}: {}", session.getId(), message.getPayload());

        JsonObject obj = JsonParser.parseString(message.getPayload()).getAsJsonObject();
        String action = obj.get("action").getAsString();
        String topic = obj.get("topic").getAsString();

        switch (action) {
            case "subscribe":
                pubSubService.subscribe(topic, session);
                break;
            case "unsubscribe":
                pubSubService.unsubscribe(topic, session);
                break;
            case "publish":
                String msg = obj.get("message").getAsString();
                pubSubService.publish(topic, msg);
                break;
            case "ping":
                session.sendMessage(new TextMessage("{\"action\":\"pong\"}"));
                break;
        }
    }
}