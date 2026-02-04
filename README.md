# Kotlin Backend Service

A lightweight Kotlin/Spring Boot backend service that simulates AWS infrastructure (EC2, S3, CloudWatch) running entirely locally. This service demonstrates production-ready patterns without requiring any external AWS services or credentials.

## Features

- **File Storage API** - Local file storage simulating Amazon S3
- **In-Memory Metrics** - CloudWatch-style metrics tracking
- **Docker Support** - Containerized deployment ready
- **Health Checks** - Production-ready health endpoints

## Quick Start

### Prerequisites

- Java 21
- Docker (for containerized deployment)

### Run with Docker

```bash
docker build -t kotlin-backend .
docker run -p 8080:8080 kotlin-backend
```

### Run Locally (Development)

```bash
./gradlew bootRun
```

## API Endpoints

### Upload File
```bash
POST /files
Content-Type: application/json

{
  "filename": "test.txt",
  "content": "Hello Nike"
}
```

### Get File
```bash
GET /files/{filename}
```

### List Files
```bash
GET /files
```

### Get Metrics
```bash
GET /metrics
```

Response:
```json
{
  "uploads": 3,
  "reads": 5,
  "errors": 1
}
```

### Health Check
```bash
GET /health
```

### Configuration

- `storage.path`: directory for local file storage (default `/storage`).
  - Env JSON: `SPRING_APPLICATION_JSON='{"storage":{"path":"./storage"}}'`
  - CLI: `--storage.path=./storage`

## API Docs

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Endpoints

- POST `/files` → `{ filename, message }`
- GET `/files/{filename}` → raw content
- GET `/files` → `["file1.txt", ...]`
- GET `/metrics` → `{ uploads, reads, errors }`
- GET `/health` → `{ status: "healthy" }`

## Error Handling

All errors return a standard body:

```
{
  "timestamp": "2024-05-01T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "code": "FILE_NOT_FOUND",
  "message": "File not found: notes.txt",
  "path": "/files/notes.txt"
}
```

## Project Structure

- `controller` — HTTP layer with OpenAPI; delegates to services.
- `service` — business logic (files, metrics, health).
- `exception` — typed exceptions, global handler, error codes.
- `model` — API models (`FileRequest`, `FileResponse`, `Metrics`, `ApiError`).

## Deploy on AWS ECS (Fargate)

Run serverlessly on ECS without managing EC2 instances.

- Build and push image to ECR:
  - `aws ecr create-repository --repository-name kotlin-backend`
  - `aws ecr get-login-password | docker login --username AWS --password-stdin <acct>.dkr.ecr.<region>.amazonaws.com`
  - `docker tag kotlin-backend:latest <acct>.dkr.ecr.<region>.amazonaws.com/kotlin-backend:latest`
  - `docker push <acct>.dkr.ecr.<region>.amazonaws.com/kotlin-backend:latest`
- Task definition (Fargate): Linux; port 8080; 0.25–1 vCPU, 512–2048 MB; CloudWatch Logs enabled.
- Service and ALB: health check path `/health`; SG allows ALB→task:8080.
- Optional persistence: EFS volume mounted at `/storage` (keep `storage.path=/storage`).

## Simulate Errors

- 404 file: `curl -i http://localhost:8080/files/does-not-exist`
- 400 JSON: `curl -i -X POST http://localhost:8080/files -H 'Content-Type: application/json' -d '{"filename":"a","content": }'`
- 500 storage: run container with `/storage` read-only, then POST upload.

