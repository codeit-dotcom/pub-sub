package org.example.controllers;

import org.example.service.PubSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private PubSubService pubSubService;

    @PostMapping("/{topic}")
    public ResponseEntity<String> createTopic(@PathVariable String topic) {
        if (pubSubService.createTopic(topic)) {
            return ResponseEntity.ok("Topic created");
        }
        return ResponseEntity.status(409).body("Topic already exists");
    }

    @DeleteMapping("/{topic}")
    public ResponseEntity<String> deleteTopic(@PathVariable String topic) {
        if (pubSubService.deleteTopic(topic)) {
            return ResponseEntity.ok("Topic deleted");
        }
        return ResponseEntity.status(404).body("Topic not found");
    }

    @GetMapping
    public Set<String> listTopics() {
        return pubSubService.listTopics();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

}
