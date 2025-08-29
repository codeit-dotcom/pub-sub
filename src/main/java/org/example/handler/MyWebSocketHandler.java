package org.example.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.example.service.PubSubService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;


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
        String action = obj.get("type").getAsString();
        String requestId = obj.has("request_id") ? obj.get("request_id").getAsString() : null;

        switch (action) {
            case "subscribe":
                String topic = obj.get("topic").getAsString();
                pubSubService.subscribe(topic, session);
                sendAck(session, requestId, topic, true);
                break;
            case "unsubscribe":
                topic = obj.get("topic").getAsString();
                pubSubService.unsubscribe(topic, session);
                sendAck(session, requestId, topic, true);
                break;
            case "publish":
                topic = obj.get("topic").getAsString();
                JsonObject messageObj = obj.getAsJsonObject("message");
                pubSubService.publish(topic, messageObj.toString());
                sendAck(session, requestId, topic, true);
                break;
            case "ping":
                try {
                    JsonObject pong = new JsonObject();
                    pong.addProperty("type", "pong");
                    pong.addProperty("request_id", "ping-abc");
                    pong.addProperty("ts", Instant.now().toString()); // current timestamp

                    session.sendMessage(new TextMessage(pong.toString()));
                } catch (Exception e) {
                    log.error("Failed to send pong", e);
                }
                break;
        }
    }

    private void sendAck(WebSocketSession session, String requestId, String topic, boolean success) {
        try {
            JsonObject ack = new JsonObject();
            ack.addProperty("type", "ack");
            ack.addProperty("request_id", requestId != null ? requestId : "");
            ack.addProperty("topic", topic != null ? topic : "");
            ack.addProperty("status", success ? "ok" : "error");
            ack.addProperty("ts", Instant.now().toString()); // current timestamp in ISO-8601

            session.sendMessage(new TextMessage(ack.toString()));
        } catch (Exception e) {
            log.error("Failed to send ack", e);
        }
    }

}