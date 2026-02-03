Project Assignment

Real-Time Release Management System with Event-Driven Architecture and AI Integration

Project Story (System Scenario)

Imagine you are working at a fast-growing software company that deploys new features every week. The company manages multiple development teams, and each team works on structured software releases that must go through a strict workflow before deployment.

Each release contains a list of tasks assigned to developers. These tasks must be completed in order, because many tasks depend on previous ones. A developer cannot simply start any task whenever they want, and they cannot work on multiple tasks at the same time, since the company wants full focus and clear accountability.

Once a release is finished, it is marked as completed and prepared for production deployment. However, real-world software is unpredictable: sometimes, after a release is completed, an urgent bug is discovered that requires an immediate hotfix. In that case, the release must be reopened, a new urgent task must be inserted, and the assigned developer must be notified immediately.

To ensure developers stay aligned, the company also provides a real-time collaboration feed where all team members can instantly see task updates, discussions, and hotfix activity as it happens. Developers can also post threaded questions under tasks, reply to each other, and hold technical discussions similar to Reddit-style message boards.

To further improve productivity, the company integrates an AI assistant that developers can chat with when they need help understanding tasks, debugging workflow issues, or asking questions about the current release. The chatbot must remember each developer’s conversation history so responses remain contextual over time.

Because this platform is mission-critical, the company requires full monitoring and analytics. Management wants dashboards that show task completion progress, Kafka event throughput, AI usage, and developer activity. If failures occur, such as Kafka downtime or repeated server errors, the system must immediately alert the administrator via email.

Finally, the entire system must be deployed using Docker Compose, reflecting how real distributed systems are run in production environments.

Your job is to build this complete backend platform.

1. Project Objective

The objective of this assignment is to design and implement a modern backend system that simulates a real DevOps-style release workflow while demonstrating advanced distributed systems concepts, including:

Workflow enforcement and state machines

Event-driven microservices using Kafka

Reactive real-time APIs using WebFlux

Background schedulers for automation

AI chatbot integration with contextual memory

Observability with Prometheus and Grafana

Alerting and notification pipelines

Secure JWT authentication and role separation

Fully containerized deployment

2. System Architecture Requirements

Your solution must be implemented as microservices.

At minimum, you must have:

Release Service

Responsible for managing releases, tasks, and workflow rules.
This service is the source of truth for task progression.

Notification Service

Responsible for consuming Kafka events and sending email notifications.
This service must remain independent and loosely coupled.

Optional additional services:

AI Chat Service

Forum/Discussion Service

3. Functional Requirements

3.1 Release and Task Workflow Management

The core of the platform is release execution.

A Release represents a software delivery milestone, and each release is composed of multiple Tasks assigned to developers.

Every task must follow a strict workflow.

Task Status States

Each task must always be in exactly one of the following states:

TODO : The task is assigned but not started

IN_PROCESS: The developer is actively working on it

COMPLETED: The task is finished

The system must prevent invalid transitions, such as completing a task that was never started.

Sequential Execution Enforcement

Tasks inside a release are ordered using an orderIndex.

The workflow must enforce:

Task N cannot be started unless Task N−1 is already COMPLETED.

This ensures release integrity and reflects real dependency chains in software development.

Example:

Task 1 must complete before Task 2 starts

Task 2 must complete before Task 3 starts

Developer Global Constraint

To prevent developers from multitasking across releases, the system must enforce:

A developer may only have one task in IN_PROCESS at any moment across the entire platform.

Before allowing a developer to start a task, the system must query MongoDB and ensure no other task is currently active.

If a violation occurs, the API must reject the request with a meaningful error response.

Release Completion Logic

A release may only be marked as completed if:

All tasks are COMPLETED

No task remains TODO or IN_PROCESS

Once completed, the release is considered closed unless reopened by hotfix.

Hotfix Workflow Requirement

In real DevOps environments, releases often need urgent fixes after completion.

Your system must support the following hotfix behavior:

If an admin adds a new task to a completed release:

The release must automatically reopen (isCompleted = false)

The new task becomes part of the release workflow

The assigned developer must immediately be notified

A Kafka event must be published announcing the hotfix

Kafka payload example:

HotfixTaskAddedEvent(developerId, releaseId, taskTitle)

This requirement ensures students implement real-world “release reopening” behavior.

3.2 Event-Driven Architecture with Kafka

The platform must use Kafka as the backbone for communication between services.

The Release Service must not send emails directly.
Instead, it publishes events, and other services react.

Required Kafka Events

Your system must publish events such as:

TaskAssignedEvent

TaskCompletedEvent

HotfixTaskAddedEvent

StaleTaskDetectedEvent

SystemErrorEvent

Each event must contain meaningful JSON payload data.

Kafka Reliability Requirements

To simulate production-grade messaging, students must implement:

At-least-once delivery guarantees

Retry topic handling

Dead Letter Queue (DLQ) for events that repeatedly fail

This ensures fault tolerance if consumers go down.

3.3 Notification and Email System

The Notification Service must consume Kafka events and send emails accordingly.

Emails must be sent when:

A developer is assigned a new task

A hotfix task is inserted into a release

A stale task reminder is triggered

A critical system error occurs

Every notification must be logged into MongoDB:

NotificationLog collection

This provides auditability.

3.4 Real-Time Activity Feed (WebFlux + SSE)

Modern teams require live visibility.

Your platform must provide a real-time “Wall of Activity” that streams updates such as:

Task started/completed

Hotfix tasks added

New forum discussions

This must be implemented using:

Spring WebFlux

Flux streaming

Server-Sent Events (SSE)

Sinks.Many broadcaster

Endpoint:

GET /activity/stream

This ensures reactive programming is used properly.

3.5 Threaded Discussion Forum

Every task must include a discussion board where developers can collaborate.

Developers must be able to:

Post questions under tasks

Reply to other comments

Reply to replies (nested threading infinitely)

This creates a Reddit-style threaded forum model.

3.6 Background Scheduler: Stale Task Detection

To prevent tasks from being forgotten, the system must include automation.

A scheduler must run periodically and detect:

Tasks in IN_PROCESS longer than 48 hours

When found:

Publish StaleTaskDetectedEvent to Kafka

Notification Service emails the developer

Log reminder in NotificationLog

This demonstrates real DevOps automation.

3.7 AI Assistant Integration (Ollama + Context Memory)

Each developer has access to an AI assistant.

The chatbot must:

Use Ollama locally with any model

Answer developer questions about releases/tasks

Maintain chat history for contextual continuity

Before sending a prompt to Ollama:

Fetch last 3–5 messages from MongoDB

Construct contextual prompt

Return response

This requirement ensures session-aware AI integration.

3.8 Monitoring and Analytics (Prometheus + Grafana)

Your services must expose metrics through Actuator + Micrometer.

Grafana dashboard must display at least 4 required analytics:

Active Developers Count

Tasks Completed Per Day

Kafka Events Published Per Minute

AI Request Rate + Latency

These dashboards must update dynamically during demo.

3.9 Alerting System (Critical Requirement)

If failures occur, administrators must be alerted immediately.

Examples:

Kafka consumer disconnected

Database connection failures

High number of HTTP 500 errors

The system must:

Publish SystemErrorEvent

Send admin alert email

Configure at least one Grafana alert rule

3.10 Security (JWT + Roles)

Authentication must be stateless using JWT.

Roles:

ADMIN

DEVELOPER

Rules:

Only admins can create releases or hotfix tasks

Developers can only update tasks assigned to them

Method-level access control required

4. Required Domains

User (Admin/Developer)

Release

Task

Comment (threaded)

ChatSession

ChatMessage

NotificationLog

5. Required REST Endpoints

Auth

POST /auth/register

POST /auth/login

Releases (Admin)

POST /releases

GET /releases

POST /releases/{id}/tasks

PATCH /releases/{id}/complete

Tasks (Developer)

GET /tasks/my

PATCH /tasks/{id}/start

PATCH /tasks/{id}/complete

Forum

POST /tasks/{id}/comments

POST /comments/{id}/reply

GET /tasks/{id}/comments

Activity Feed

GET /activity/stream

Chatbot

POST /chat/session

POST /chat/{sessionId}/message

GET /chat/{sessionId}/history

Testing Requirements

The project must include comprehensive automated testing. Core business rules, workflow constraints, and critical service logic must be validated through meaningful unit and integration tests. In addition, all major REST API endpoints must be tested using RestAssured, ensuring correct request/response behavior, proper enforcement of security rules, and overall reliability of the system.

Deployment Requirements

Docker Compose must run:

MongoDB

Kafka + Zookeeper

Prometheus

Grafana

Release Service

Notification Service

Ollama (optional)

Command:

docker-compose up --build

7. Deliverables

GitHub repository

Final report

Demo video (5–10 min)

ERD + schema + dashboards

System architecture – (microservices)

Flexibility and Extensions

Students are encouraged to treat this specification as a realistic foundation rather than a rigid limitation. You may add, remove, or modify domains, properties, endpoints, background activities, or supporting services as needed, provided that your design decisions are clearly justified and aligned with the overall system goals. Any extensions or simplifications must be documented in your final report with a clear rationale explaining how they support the functionality and architecture of your system. You have full architectural freedom; however, the system must consist of at least two microservices. Please provide a clear rationale for your chosen design. Below are examples that might help you.
