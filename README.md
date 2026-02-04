# Kotlin Backend (Spring Boot)

Minimal file storage service built with Kotlin and Spring Boot. Endpoints to upload, read, and list files, plus health and metrics. OpenAPI docs included via springdoc.

## Requirements

- Java 21
- Gradle wrapper (included)

## Quick Start

- Run locally: `./gradlew bootRun`
- Build JAR: `./gradlew build`
- Run with Docker:
  - `docker build -t kotlin-backend .`
  - `docker run --rm -p 8080:8080 -v $(pwd)/storage:/storage kotlin-backend`

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

