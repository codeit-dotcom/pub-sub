# In-Memory Pub/Sub System (Java + Spring Boot + WebSockets)

This is a simplified Pub/Sub system implemented in Java using Spring Boot.  
It provides:

- WebSocket endpoint (`/ws`) for publishing, subscribing, unsubscribing, and ping.
- REST APIs for topic management (create, delete, list), health checks, and stats.
- Multi-publisher and multi-subscriber support.
- In-memory state only (no database or external broker).

---

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