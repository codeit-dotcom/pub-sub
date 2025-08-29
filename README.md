# In-Memory Pub/Sub System (Java + Spring Boot + WebSockets)

This is a simplified Pub/Sub system implemented in Java using Spring Boot.
It provides:

- WebSocket endpoint (`/ws`) for publishing, subscribing, unsubscribing, and ping.
- REST APIs for topic management (create, delete, list), health, and stats.
- Multi-publisher, multi-subscriber support.
- In-memory state only (no DB or external broker).

---

## 🚀 Setup & Run

### Build locally
```bash
mvn clean package
java -jar target/pubsub-service-0.0.1-SNAPSHOT.jar

