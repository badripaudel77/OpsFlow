# Auth Service

Authentication microservice for OpsFlow - handles user registration, login, and JWT token management.

## Port

- **8085**

## Endpoints

| Method | Endpoint         | Auth | Description           |
| ------ | ---------------- | ---- | --------------------- |
| POST   | `/auth/register` | No   | Register new user     |
| POST   | `/auth/login`    | No   | Login and get JWT     |
| POST   | `/auth/validate` | No   | Validate JWT token    |
| POST   | `/auth/refresh`  | No   | Refresh expired token |
| GET    | `/auth/me`       | Yes  | Get current user info |

## Configuration

| Variable                 | Default                                  | Description                       |
| ------------------------ | ---------------------------------------- | --------------------------------- |
| `MONGO_URI`              | `mongodb://localhost:27017/opsflow_auth` | MongoDB connection                |
| `JWT_SECRET`             | -                                        | JWT signing secret (min 256 bits) |
| `JWT_EXPIRATION`         | `86400000`                               | Token expiry (24h in ms)          |
| `JWT_REFRESH_EXPIRATION` | `604800000`                              | Refresh token expiry (7d)         |

## Run Locally

```bash
./mvnw spring-boot:run
```

## Docker

```bash
docker compose up -d auth-service
```
