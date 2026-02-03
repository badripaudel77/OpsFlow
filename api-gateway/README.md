# API Gateway

Spring Cloud Gateway for OpsFlow - single entry point for all client requests with JWT validation.

## Port

- **8080**

## Features

- JWT token validation
- User claims extraction (adds `X-User-Id`, `X-User-Email`, `X-User-Role` headers)
- CORS configuration
- Request logging with correlation IDs
- SSE passthrough support

## Routes

| Path                       | Target Service            | Auth Required |
| -------------------------- | ------------------------- | ------------- |
| `/auth/register`           | auth-service:8085         | No            |
| `/auth/login`              | auth-service:8085         | No            |
| `/auth/validate`           | auth-service:8085         | No            |
| `/auth/refresh`            | auth-service:8085         | No            |
| `/auth/me`                 | auth-service:8085         | Yes           |
| `/api/v1/releases/**`      | release-service:8081      | Yes           |
| `/api/v1/tasks/**`         | release-service:8081      | Yes           |
| `/api/v1/activity/**`      | release-service:8081      | Yes (SSE)     |
| `/api/v1/notifications/**` | notification-service:8082 | Yes           |
| `/api/v1/chat/**`          | chat-service:8083         | Yes           |
| `/api/v1/comments/**`      | forum-service:8084        | Yes           |

## Configuration

| Variable              | Default                 | Description                                  |
| --------------------- | ----------------------- | -------------------------------------------- |
| `JWT_SECRET`          | -                       | JWT signing secret (must match auth-service) |
| `AUTH_SERVICE_URL`    | `http://localhost:8085` | Auth service URL                             |
| `RELEASE_SERVICE_URL` | `http://localhost:8081` | Release service URL                          |

## Run Locally

```bash
./mvnw spring-boot:run
```

## Docker

```bash
docker compose up -d api-gateway
```
