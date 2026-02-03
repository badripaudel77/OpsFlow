# Real-Time Release Management System - Architecture Design

## Table of Contents

1. [System Overview](#system-overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Microservices Description](#microservices-description)
4. [Authentication & Security](#authentication--security)
5. [Event-Driven Architecture](#event-driven-architecture)
6. [Data Model](#data-model)
7. [Implementation Approach](#implementation-approach)
8. [Technology Stack](#technology-stack)
9. [Deployment Strategy](#deployment-strategy)

---

## System Overview

The Real-Time Release Management System is a distributed, event-driven platform designed to manage software releases with strict workflow enforcement, real-time collaboration, AI assistance, and comprehensive monitoring. The system enforces sequential task execution, prevents developer multitasking, and provides instant notifications and live activity feeds.

### Key Characteristics

- **Microservices Architecture**: 5 independent services with clear boundaries
- **Event-Driven Communication**: Kafka-based asynchronous messaging
- **Reactive Programming**: WebFlux for real-time streaming
- **Stateless Authentication**: JWT-based security
- **Full Observability**: Prometheus metrics + Grafana dashboards
- **Containerized Deployment**: Docker Compose orchestration

---

## Architecture Diagram

## ![Architecture Diagram](documents/Architecture.png)

## Microservices Description

### 1. API Gateway (Port: 8080)

**Technology**: Spring Cloud Gateway / Spring WebFlux

**Responsibilities**:

- Single entry point for all client requests
- JWT token validation and extraction
- Route requests to appropriate microservices
- Rate limiting and throttling
- CORS configuration
- Request/response logging
- Circuit breaker patterns (Resilience4j)

**Key Routes**:

```
/auth/**          → Auth Service
/api/releases/**  → Release Service
/api/tasks/**     → Release Service
/api/activity/**  → Release Service (SSE)
/api/comments/**  → Forum Service
/api/chat/**      → AI Chat Service
```

**Security Filter Chain**:

```
Request → CORS Filter → JWT Filter → Authorization Filter → Route
```

---

### 2. Release Service (Port: 8081)

**Technology**: Spring Boot + WebFlux + MongoDB

**Core Responsibilities**:

- Manage releases lifecycle (create, list, complete)
- Manage tasks with strict workflow enforcement
- Validate sequential execution (orderIndex-based)
- Enforce developer global constraint (one active task)
- Handle hotfix workflow (reopen releases)
- Publish Kafka events for all state changes
- Provide real-time activity feed via Server-Sent Events (SSE)
- Schedule stale task detection (48-hour check)

**Key Business Rules**:

1. Task state transitions: `TODO → IN_PROCESS → COMPLETED`
2. Task N cannot start unless Task N-1 is completed
3. Developer can only have one `IN_PROCESS` task globally
4. Release completion requires all tasks to be `COMPLETED`
5. Adding task to completed release reopens it automatically

**REST Endpoints**:

```
POST   /api/releases              [ADMIN]
GET    /api/releases              [ADMIN, DEVELOPER]
POST   /api/releases/{id}/tasks   [ADMIN]
PATCH  /api/releases/{id}/complete [ADMIN]
GET    /api/tasks/my              [DEVELOPER]
PATCH  /api/tasks/{id}/start      [DEVELOPER]
PATCH  /api/tasks/{id}/complete   [DEVELOPER]
GET    /api/activity/stream       [ALL] - SSE
```

**Background Scheduler**:

- Runs every 6 hours (configurable)
- Queries tasks in `IN_PROCESS` older than 48 hours
- Publishes `StaleTaskDetectedEvent` to Kafka

**Kafka Producer Events**:

- `TaskAssignedEvent`
- `TaskStartedEvent`
- `TaskCompletedEvent`
- `HotfixTaskAddedEvent`
- `StaleTaskDetectedEvent`
- `ReleaseCompletedEvent`
- `SystemErrorEvent`

---

### 3. Notification Service (Port: 8082)

**Technology**: Spring Boot + Kafka Consumer + JavaMail

**Core Responsibilities**:

- Consume Kafka events from topics
- Send email notifications via SMTP
- Log all notifications in MongoDB (`notificationLogs` collection)
- Implement retry logic with Dead Letter Queue (DLQ)
- Handle transient failures gracefully

**Kafka Topics Consumed**:

- `task-assigned`
- `hotfix-added`
- `stale-task-detected`
- `system-error`

**Email Templates**:

```
1. Task Assignment Email
   Subject: New Task Assigned - {taskTitle}
   Body: Developer name, release info, task details, deadline

2. Hotfix Alert Email
   Subject: URGENT: Hotfix Task Added to {releaseName}
   Body: Release reopened, new task details

3. Stale Task Reminder
   Subject: Task Reminder - {taskTitle} (In Progress for 48+ hours)
   Body: Task details, started date, call to action

4. System Error Alert
   Subject: CRITICAL: System Error Detected
   To: Admin
   Body: Error details, timestamp, affected service
```

---

### 4. AI Chat Service (Port: 8083)

**Technology**: Spring Boot + Ollama Client + MongoDB

**Core Responsibilities**:

- Manage chat sessions per developer
- Maintain conversation history for context
- Integrate with local Ollama instance
- Construct contextual prompts using message history
- Store all chat interactions in MongoDB

**Implementation Flow**:

```
1. Developer creates chat session
   → Store ChatSession in MongoDB

2. Developer sends message
   → Store user message in chatMessages collection
   → Fetch last 3-5 messages from session
   → Build contextual prompt:
      System: "You are an AI assistant for a release management system..."
      User (3 msgs ago): "What is release 5?"
      Assistant: "Release 5 is..."
      User (current): "What tasks are in it?"
   → Send to Ollama API
   → Store assistant response
   → Return response to client

3. Developer views history
   → Query chatMessages by sessionId
   → Return sorted by timestamp
```

**REST Endpoints**:

```
POST /api/chat/session                    [DEVELOPER]
POST /api/chat/{sessionId}/message        [DEVELOPER]
GET  /api/chat/{sessionId}/history        [DEVELOPER]
GET  /api/chat/sessions                   [DEVELOPER] - list all sessions
DELETE /api/chat/{sessionId}              [DEVELOPER] - clear session
```

**Context Window Management**:

- Keep last 5 messages (configurable)
- If token limit exceeded, summarize older messages
- System message always included for context

---

### 5. Forum Service (Port: 8084)

**Technology**: Spring Boot + MongoDB

**Core Responsibilities**:

- Manage threaded comments under tasks
- Support infinite nesting (Reddit-style)
- Store comment metadata (author, timestamp, parent)
- Validate user permissions (can only comment on assigned/visible tasks)

**REST Endpoints**:

```
POST /api/comments/task/{taskId}           [DEVELOPER] - create root comment
POST /api/comments/{commentId}/reply       [DEVELOPER] - reply to comment
GET  /api/comments/task/{taskId}           [DEVELOPER] - get all (nested)
PATCH /api/comments/{commentId}            [DEVELOPER] - edit own comment
DELETE /api/comments/{commentId}           [DEVELOPER] - delete own comment
```

---

### 6. Auth Service (Port: 8085)

**Technology**: Spring Boot + Spring Security + JWT + BCrypt

**Core Responsibilities**:

- User registration with password hashing (BCrypt)
- User authentication and JWT token generation
- Token validation endpoint (for Gateway)
- Role management (ADMIN, DEVELOPER)
- Password reset (optional)

**JWT Token Structure**:

```json
{
  "sub": "user123",
  "email": "developer@example.com",
  "role": "DEVELOPER",
  "iat": 1675344000,
  "exp": 1675430400
}
```

**REST Endpoints**:

```
POST /auth/register    - Create new user
POST /auth/login       - Authenticate and get JWT
POST /auth/validate    - Validate JWT (used by Gateway)
POST /auth/refresh     - Refresh expired token
```

**Registration Flow**:

```
1. Client sends: { email, password, fullName, role }
2. Validate email uniqueness
3. Hash password with BCrypt (strength 12)
4. Store in MongoDB users collection
5. Return success (no auto-login)
```

**Login Flow**:

```
1. Client sends: { email, password }
2. Find user by email
3. Verify password with BCrypt
4. Generate JWT with user claims
5. Return: { token, expiresIn, user }
```

**Token Validation** (called by Gateway):

```
1. Extract token from Authorization header
2. Verify signature with secret key
3. Check expiration
4. Return user claims if valid
5. Gateway attaches user info to request context
```

---

## Authentication & Security

### Security Implementation Details

**1. JWT Configuration**:

- Algorithm: HS256 (HMAC-SHA256)
- Secret: Stored in environment variable
- Expiration: 24 hours (configurable)
- Refresh token: 7 days (optional)

**2. Role-Based Access Control**:

| Endpoint                      | ADMIN | DEVELOPER      |
| ----------------------------- | ----- | -------------- |
| POST /releases                | ✅    | ❌             |
| POST /releases/{id}/tasks     | ✅    | ❌             |
| PATCH /releases/{id}/complete | ✅    | ❌             |
| GET /releases                 | ✅    | ✅             |
| GET /tasks/my                 | ❌    | ✅             |
| PATCH /tasks/{id}/start       | ❌    | ✅ (own tasks) |
| PATCH /tasks/{id}/complete    | ❌    | ✅ (own tasks) |
| POST /comments/{taskId}       | ❌    | ✅             |
| GET /activity/stream          | ✅    | ✅             |
| POST /chat/session            | ❌    | ✅             |

---

## Data Model

### MongoDB Collections

**1. users Collection**:

```json
{
  "_id": "user123",
  "email": "john@example.com",
  "password": "$2a$12$hashed...",
  "fullName": "John Doe",
  "role": "DEVELOPER",
  "createdAt": "2026-01-15T10:00:00Z",
  "isActive": true
}
```

**Indexes**: `email (unique)`, `role`

---

**2. releases Collection**:

```json
{
  "_id": "release456",
  "name": "Sprint 23 Release",
  "description": "Q1 feature release",
  "version": "2.3.0",
  "isCompleted": false,
  "createdBy": "admin001",
  "createdAt": "2026-02-01T09:00:00Z",
  "completedAt": null,
  "tasks": [
    {
      "taskId": "task001",
      "orderIndex": 1
    },
    {
      "taskId": "task002",
      "orderIndex": 2
    }
  ]
}
```

**Indexes**: `isCompleted`, `createdAt`, `tasks.taskId`

---

**3. tasks Collection**:

```json
{
  "_id": "task001",
  "title": "Implement JWT authentication",
  "description": "Add stateless authentication using JWT tokens",
  "releaseId": "release456",
  "orderIndex": 1,
  "status": "COMPLETED",
  "assignedTo": "dev789",
  "createdAt": "2026-02-01T09:15:00Z",
  "startedAt": "2026-02-01T10:00:00Z",
  "completedAt": "2026-02-01T16:30:00Z",
  "isHotfix": false
}
```

**Indexes**: `releaseId`, `assignedTo`, `status`, `startedAt`, `status + assignedTo (compound)`

---

**4. comments Collection**:

```json
{
  "_id": "comment001",
  "taskId": "task001",
  "userId": "dev789",
  "content": "Should we use HS256 or RS256 for JWT?",
  "parentCommentId": null,
  "createdAt": "2026-02-01T11:00:00Z",
  "updatedAt": null
}
```

```json
{
  "_id": "comment002",
  "taskId": "task001",
  "userId": "dev456",
  "content": "HS256 is simpler for this use case",
  "parentCommentId": "comment001",
  "createdAt": "2026-02-01T11:15:00Z",
  "updatedAt": null
}
```

**Indexes**: `taskId`, `parentCommentId`, `taskId + parentCommentId (compound)`

---

**5. chatSessions Collection**:

```json
{
  "_id": "session001",
  "userId": "dev789",
  "title": "Help with task workflow",
  "createdAt": "2026-02-02T10:00:00Z",
  "lastMessageAt": "2026-02-02T10:15:00Z",
  "isActive": true
}
```

**Indexes**: `userId`, `createdAt`

---

**6. chatMessages Collection**:

```json
{
  "_id": "msg001",
  "sessionId": "session001",
  "role": "user",
  "content": "How does the task workflow work?",
  "timestamp": "2026-02-02T10:00:00Z"
}
```

```json
{
  "_id": "msg002",
  "sessionId": "session001",
  "role": "assistant",
  "content": "Tasks follow a sequential workflow: TODO → IN_PROCESS → COMPLETED. You can only start task N if task N-1 is completed.",
  "timestamp": "2026-02-02T10:00:15Z"
}
```

**Indexes**: `sessionId + timestamp (compound)`

---

**7. notificationLogs Collection**:

```json
{
  "_id": "notif001",
  "type": "TASK_ASSIGNED",
  "recipientEmail": "john@example.com",
  "subject": "New Task Assigned - Implement JWT",
  "body": "You have been assigned...",
  "sentAt": "2026-02-01T09:20:00Z",
  "status": "SENT",
  "eventId": "event123",
  "metadata": {
    "taskId": "task001",
    "releaseId": "release456"
  }
}
```

**Indexes**: `recipientEmail`, `sentAt`, `status`, `type`

---

### Entity Relationships

![Entity Relationships](documents/EntityDiagram.png)

### Run Commands

```bash
# Build and start all services
docker-compose up --build

# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Clean volumes (reset data)
docker-compose down -v

# Scale notification service (if needed)
docker-compose up -d --scale notification-service=3
```

### Health Checks

All services expose health endpoints:

```
http://localhost:8080/actuator/health  (Gateway)
http://localhost:8081/actuator/health  (Release Service)
http://localhost:8082/actuator/health  (Notification Service)
http://localhost:8083/actuator/health  (AI Chat Service)
http://localhost:8084/actuator/health  (Forum Service)
http://localhost:8085/actuator/health  (Auth Service)
```

## Summary

This architecture provides a comprehensive, production-ready design for the Real-Time Release Management System with:

✅ **5 Microservices**: Gateway, Auth, Release, Notification, AI Chat, Forum  
✅ **Event-Driven**: Kafka-based async communication with DLQ  
✅ **Reactive Programming**: WebFlux + SSE for real-time feeds  
✅ **Security**: JWT authentication with role-based access  
✅ **AI Integration**: Ollama with contextual chat history  
✅ **Monitoring**: Prometheus + Grafana with custom metrics  
✅ **Alerting**: Email notifications + Grafana alerts  
✅ **Containerization**: Full Docker Compose deployment  
✅ **Testing**: Unit, integration, and API tests with RestAssured

The system enforces strict workflow rules, prevents developer multitasking, supports hotfix reopening, and provides comprehensive observability—meeting all requirements specified in the project document.
