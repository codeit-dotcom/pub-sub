# Pub/Sub WebSocket Broker

This project implements a lightweight **publish/subscribe (pub/sub) broker** over WebSockets in Java.  
It allows clients to:

- **Publish** messages to topics
- **Subscribe** to topics
- **Receive** messages asynchronously with backpressure handling
- **Inspect broker statistics** via a REST endpoint (`GET /stats`)

---

## Design Overview

### Topics and Subscriptions
- A **topic** represents a logical channel for messages (e.g., `orders`, `payments`).
- Each topic maintains:
    - A list of **messages** published
    - A set of **subscribers** (WebSocket sessions) listening for updates
- Topics are stored in:
  ```java
  private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();
   ```

### Backpressure Policy

To avoid overwhelming slow subscribers, each subscriber is assigned a bounded queue:

- Each subscriber has a queue of at most 10 pending messages.
- If the queue is full:
    - New messages for that subscriber are dropped (to protect broker stability).
    - This ensures that one slow consumer does not block others, keeping the system responsive and stable.


```java
private static final int SUBSCRIBER_QUEUE_SIZE = 10; // Backpressure limit
private final BlockingQueue<String> queue; // bounded for backpressure
```

### Running State

Broker maintains a running flag to gracefully shut down:
```java
private volatile boolean running = true;
```

## 🚀 Setup & Run

### Build locally
To build and run the service using Maven and Java:

```bash
mvn clean package
java -jar target/pubsub-service-0.0.1-SNAPSHOT.jar

```

### Build and run with Docker
```bash
docker build -t pubsub-service .
docker run -p 8080:8080 pubsub-service

```