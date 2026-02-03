# Chatbot Service

AI-powered assistant for OpsFlow that helps developers get information about releases, tasks, and deployments through natural language conversations using Ollama (llama3.2:1b).

## 🎯 Features

- **Natural Language Interface**: Ask questions in plain English about your releases and tasks
- **Contextual Awareness**: Automatically includes relevant release and task data in responses
- **Session Management**: Maintains conversation history for context
- **Ollama Integration**: Local LLM inference with llama3.2:1b model
- **Fallback Handling**: Graceful degradation when AI service is unavailable
- **Stateless Authentication**: JWT-based security (simplified for MVP)

## 🏗️ Architecture

### Components

- **ChatController**: REST API endpoints for chat interactions
- **ChatService**: Core business logic for session and message management
- **OllamaService**: HTTP client for Ollama LLM with retry logic
- **ReleaseContextService**: Fetches release/task context from MongoDB
- **Repositories**: MongoDB data access for sessions, messages, and context

### Data Flow

```
User Request → API Gateway (/api/v1/chat/**)
              ↓
           ChatController
              ↓
           ChatService (builds context from MongoDB)
              ↓
           OllamaService (sends to Ollama LLM)
              ↓
           Response (saved to MongoDB)
```

## 📋 Prerequisites

- Docker and Docker Compose (for containerized deployment)
- Ollama with llama3.2:1b model pulled
- MongoDB running
- Java 21 (for local development)

## 🚀 Quick Start

### Using Docker Compose (Recommended)

From the project root:

```bash
# Start all services including chatbot
docker compose up -d

# Pull the Ollama model (first time only)
docker exec -it ollama ollama pull llama3.2:1b

# Check chatbot service health
curl http://localhost:8085/api/v1/chat/actuator/health
```

### Local Development

```bash
# Start MongoDB locally
docker run -d -p 27017:27017 --name mongodb mongo:7.0

# Start Ollama locally
docker run -d -p 11434:11434 --name ollama ollama/ollama:latest
docker exec -it ollama ollama pull llama3.2:1b

# Run the application
cd chatbot-service
./mvnw spring-boot:run
```

## 🔌 API Endpoints

All endpoints are accessible through the API Gateway at `http://localhost:8085/api/v1/chat`

**Authentication Required:** All requests must include a valid JWT token in the Authorization header.

```bash
# Login first to get JWT token
TOKEN=$(curl -s -X POST http://localhost:8085/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "john.doe", "password": "password123"}' \
  | jq -r '.token')
```

### 1. Create Chat Session

**POST** `/chat/session`

Create a new chat session for the authenticated user. User ID is automatically extracted from JWT.

**Request:**

```bash
curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Q1 2026 Release Discussion"
  }'
```

**Response:**

```json
{
  "sessionId": "65f8a1b2c3d4e5f6g7h8i9j0",
  "userId": "user-123",
  "title": "Q1 2026 Release Discussion",
  "createdAt": "2026-02-02T10:30:00Z"
}
```

### 2. Send Message

**POST** `/chat/{sessionId}/message`

Send a message to the chatbot and get a response. Session ownership is automatically verified.

**Request:**

```bash
curl -X POST http://localhost:8085/api/v1/chat/65f8a1b2c3d4e5f6g7h8i9j0/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What are the pending tasks in this release?"
  }'
```

**Response:**

```json
{
  "messageId": "65f8a1b2c3d4e5f6g7h8i9j1",
  "response": "Based on the current release context, there are 3 pending tasks:\n\n1. API Integration Testing (assigned to John)\n2. Database Migration (assigned to Sarah)\n3. UI Component Review (unassigned)\n\nWould you like more details about any of these tasks?",
  "timestamp": "2026-02-02T10:31:00Z"
}
```

### 3. Get Chat History

**GET** `/chat/{sessionId}/history`

Retrieve the complete conversation history for a session. Only accessible to the session owner.

**Request:**

```bash
curl "http://localhost:8085/api/v1/chat/65f8a1b2c3d4e5f6g7h8i9j0/history?limit=50&offset=0" \
  -H "Authorization: Bearer $TOKEN"
```

**Response:**

```json
{
  "sessionId": "65f8a1b2c3d4e5f6g7h8i9j0",
  "userId": "user-123",
  "messages": [
    {
      "role": "user",
      "content": "What are the pending tasks in this release?",
      "timestamp": "2026-02-02T10:31:00Z"
    },
    {
      "role": "assistant",
      "content": "Based on the current release context, there are 3 pending tasks...",
      "timestamp": "2026-02-02T10:31:05Z"
    }
  ]
}
```

### 4. Get User Sessions

`

List all chat sessions for the authenticated user. User ID is automatically extracted from JWT.

**Request:**

```bash
curl http://localhost:8085/api/v1/chat/sessions \
  -H "Authorization: Bearer $TOKEN"
```

**Response:**

```json
{
  "userId": "user-123",
  "sessions": [
    {
      "sessionId": "65f8a1b2c3d4e5f6g7h8i9j0",
      "title": "Q1 2026 Release Discussion4e5f6g7h8i9j0",
      "releaseId": "release-456",
      "createdAt": "2026-02-02T10:30:00Z",
      "lastActivity": "2026-02-02T10:31:05Z"
    }
  ]
}
```

## 🧪 Testing Guide

Prerequisites: Get JWT Token

All test scenarios require authentication. First, register and login:

```bash
# Register a new user
curl -X POST http://localhost:8085/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test-user",
    "email": "test@example.com",
    "password": "Password123!",
    "roles": ["USER"]
  }'

# Login and save token
TOKEN=$(curl -s -X POST http://localhost:8085/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test-user", "password": "Password123!"}' \
  | jq -r '.token')

echo "Token: $TOKEN"
```

### Test Scenario 1: Basic Conversation

```bash
# Step 1: Create a session (no userId needed - extracted from JWT)
SESSION_RESPONSE=$(curl -s -X POST http://localhost:8085/api/v1/chat/session \
  -H "Authorization: Bearer $TOKEN" \authenticated user
curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Release Planning"}'

curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Bug Tracking"}'

# List all my sessions
curl http://localhost:8085/api/v1/chat/sessions \
  -H "Authorization: Bearer $TOKEN
  -d '{"message": "What is this release about?"}'

# Step 3: Follow-up question
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message": "Are there any high priority tasks?"}'
Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message": "Show me all tasks assigned to John"}'

# Release status queries
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message": "What is the current status of this release?"}'

# General help
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Authorization: Bearer $TOKEN"
# Create multiple sessions for the same user
curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Content-Type: application/json" \
  -d '{"userId": "test-user", "releaseId": "release-001"}'

curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Content-Type: application/json" \
  -d '{"userId": "test-user", "releaseId": "release-002"}'

# List all sessions
curl "http://localhost:8085/api/v1/chat/sessions?userId=test-user" | jq '.'
```

### Test Scenario 3: Contextual Queries

```bash
# Assumes you have releases and tasks in MongoDB
SESSION_ID="<your-session-id>"

# Task-related queries
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Content-Type: application/json" \
  -d '{"message": "Show me all tasks assigned to John"}'

# Release status queries
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Content-Type: application/json" \
  -d '{"message": "What is the current status of this release?"}'

# General help
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Content-Type: application/json" \
  -d '{"message": "/missing token
curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Content-Type: application/json" \
  -d '{"title": "Test"}'

# Expected: 401 Unauthorized (from API Gateway)

# Test with invalid session ID
curl -X POST http://localhost:8085/api/v1/chat/invalid-session-id/message \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'

# Expected: 404 Not Found or 403 Forbidden

# Test accessing another user's session
# (Create session with one user, try to access with another)
# Test with empty message
curl -X POST http://localhost:8085/api/v1/chat/$SESSION_ID/message \
  -H "Content-Type: application/json" \
  -d '{"message": ""}'

# Expected: 400 Bad Request
```

## 🔍 Monitoring

### Health Check

```bash
# Direct service health
curl http://localhost:8084/actuator/health

# Through API Gateway
curl http://localhost:8085/api/v1/chat/actuator/health
```

### Prometheus Metrics

```bash
# View metrics
curl http://localhost:8084/actuator/prometheus

# Key metrics to monitor:
# - http_server_requests_seconds_* (request latency)
# - jvm_memory_* (memory usage)
# - chatbot_messages_total (custom metric - if implemented)
```

### Logs

```bash
# View chatbot service logs
docker compose logs -f chatbot-service

# View Ollama logs
docker compose logs -f ollama

# Search for errors
docker compose logs chatbot-service | grep ERROR
```

## 🛠️ Configuration

Key configuration properties in `application.yaml`:

```yaml
server:
  port: 8084

spring:
  mongodb:
    uri: mongodb://localhost:27017/flow_ops_db

ollama:
  base-url: http://localhost:11434
  model: llama3.2:1b
  timeout: 30000 # 30 seconds
  retry:
    max-attempts: 2
    delay: 1000 # 1 second

chat:
  context:
    max-messages: 5 # Last N messages for context
```

Environment variables override (Docker):

- `SERVER_PORT`
- `SPRING_DATA_MONGODB_URI`
- `OLLAMA_BASE_URL`
- `OLLAMA_MODEL`
- `OLLAMA_TIMEOUT`
- `OLLAMA_MAX_RETRIES`

## 🐛 Troubleshooting

### Issue: Chatbot responds with "I'm having trouble connecting to the AI service"

**Cause**: Ollama service is not running or model not pulled

**Solution**:

```bash
# Check if Ollama is running
docker ps | grep ollama

# Pull the model
docker exec -it ollama ollama pull llama3.2:1b

# Verify model is available
docker exec -it ollama ollama list
```

### Issue: Session not found error

**Cause**: Invalid session ID or session expired

**Solution**:

```bash
# Create a new session
curl -X POST http://localhost:8085/api/v1/chat/session \
  -H "Content-Type: application/json" \
  -d '{"userId": "your-user-id"}'
```

### Issue: Slow responses

**Cause**: Ollama model taking time to generate response

**Solution**:

- Use a smaller model (llama3.2:1b is already small)
- Increase `OLLAMA_TIMEOUT` in environment variables
- Check system resources (CPU/RAM)

### Issue: Connection refused to MongoDB

**Cause**: MongoDB not running or wrong connection string

**Solution**:

```bash
# Check MongoDB is running
docker ps | grep mongodb

# Test connection
docker exec -it mongodb mongosh --eval "db.adminCommand('ping')"

# Verify connection string in docker-compose.yml
# Should be: mongodb://mongodb:27017/flow_ops_db
```

## 📚 Example Questions to Ask

The chatbot can help with:

- **Task Information**: "WhaJWT authentication with gateway-level validation:

- **API Gateway**: Validates JWT token, extracts user information, passes via headers
- **Chatbot Service**: Reads user ID from `X-User-Id` header (trusted internal communication)
- **Session Isolation**: Each user can only access their own chat sessions
- **Ownership Verification**: All operations verify session belongs to the authenticated user
- **Stateless**: No session state stored in service, all context from MongoDB

**Security Flow:**

```
1. User logs in → Receives JWT token
2. Request with JWT → API Gateway validates
3. Gateway extracts userId from JWT
4. Gateway adds X-User-Id header
5. Chatbot reads header → Verifies session ownership
6. Response returned to user
```

**Note**: Services within the Docker network trust headers from the API Gateway. External access is only possible through the gateway
Current implementation uses simplified JWT authentication:

- Stateless session management
- HTTP Basic authentication (temporary - to be replaced with full JWT)
- All endpoints require authentication

**Note**: For production, implement proper JWT token validation against auth-service.

## 📈 Performance

- **Average Response Time**: 2-5 seconds (depends on Ollama inference time)
- **Retry Logic**: 2 attempts with 1-second delay
- **Timeout**: 30 seconds per Ollama request
- **Concurrent Sessions**: Limited by MongoDB and Ollama capacity

## 🚀 Future Enhancements

- [ ] WebSocket support for real-time streaming responses
- [ ] Kafka integration for event-driven context updates
- [ ] Rate limiting per user
- [ ] Conversation summarization for long sessions
- [ ] Multi-language support
- [ ] Voice input/output
- [ ] Proactive notifications based on events

## 📞 Support

For issues or questions:

1. Check logs: `docker compose logs chatbot-service`
2. Verify Ollama model: `docker exec -it ollama ollama list`
3. Test health endpoint: `curl http://localhost:8084/actuator/health`
