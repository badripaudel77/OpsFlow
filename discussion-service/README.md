# Discussion Service

A microservice for managing discussions and comments in the OpsFlow platform.

## Prerequisites
- Java 22
- Maven
- Docker (for MongoDB)
- Access to shared Kafka cluster

## Architecture

This service is part of the OpsFlow microservices architecture:
- **MongoDB**: Service's own database (local Docker container)
- **Kafka**: Shared infrastructure (runs on a separate server, NOT bundled here)

## Run the Project

### 1. Start MongoDB
```bash
docker-compose up -d
```

### 2. Run the Application

**Development (local Kafka):**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.kafka.bootstrap-servers=localhost:9092"
```

**Production (shared Kafka cluster):**
```bash
# Set environment variables
export KAFKA_BOOTSTRAP_SERVERS=kafka-server:9092
export MONGODB_URI=mongodb://mongo-server:27017/discussion_db

mvn spring-boot:run
```

Or pass as arguments:
```bash
java -jar target/discussion-service.jar \
  --spring.kafka.bootstrap-servers=kafka-server:9092 \
  --spring.data.mongodb.uri=mongodb://mongo-server:27017/discussion_db
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `KAFKA_BOOTSTRAP_SERVERS` | localhost:9092 | Kafka cluster address |
| `MONGODB_URI` | mongodb://localhost:27017/discussion_db | MongoDB connection URI |

## Access URLs

| Service | URL |
|---------|-----|
| Discussion Service API | http://localhost:8083/api/discussions |
| Swagger UI | http://localhost:8083/swagger-ui.html |
| API Docs | http://localhost:8083/api-docs |
| Health Check | http://localhost:8083/actuator/health |

## API Endpoints

### Discussions
- `POST /api/discussions` - Create discussion
- `GET /api/discussions/{id}` - Get discussion
- `PUT /api/discussions/{id}` - Update discussion
- `DELETE /api/discussions/{id}` - Delete discussion
- `GET /api/discussions/release/{releaseId}` - Get by release
- `GET /api/discussions/task/{taskId}` - Get by task
- `POST /api/discussions/{id}/resolve` - Resolve discussion
- `POST /api/discussions/{id}/reopen` - Reopen discussion

### Comments
- `POST /api/discussions/{id}/comments` - Add comment
- `GET /api/discussions/{id}/comments` - Get comments
- `PUT /api/discussions/{id}/comments/{commentId}` - Update comment
- `DELETE /api/discussions/{id}/comments/{commentId}` - Delete comment

## Kafka Topics

**Produces:**
- `discussion-created-topic`
- `comment-added-topic`
- `discussion-resolved-topic`

**Consumes:**
- `task-assigned-topic`
- `hotfix-task-added-topic`
- `task-completed-topic`

## Stop Services
```bash
docker-compose down
```
