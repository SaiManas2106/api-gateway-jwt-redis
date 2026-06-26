# API Gateway Security Platform

Production-style Spring Cloud Gateway project for centralized authentication, authorization, traffic control, resilience, and observability across microservices.

The project currently contains:

- `api-gateway` - Spring Cloud Gateway edge service
- `user-service` - identity and user profile service
- `product-service` - protected downstream product API
- `redis` - backing store for gateway rate limiting

## Current Capabilities

- JWT-protected gateway routes
- Redis-backed request rate limiting
- Resilience4j circuit breakers and fallback endpoints
- Docker Compose local orchestration
- Maven aggregator build from the repository root

## Build

```bash
mvn test
```

## Run

```bash
mvn -pl api-gateway,user-service,product-service -DskipTests package
docker compose up --build
```

## Local URLs

- Gateway: `http://localhost:8080`
- User service: `http://localhost:8082`
- Product service: `http://localhost:8081`
- Gateway actuator health: `http://localhost:8080/actuator/health`

This branch upgrades the project into a deeper API gateway platform with real auth, route-level authorization, identity-aware rate limiting, tracing, audit-friendly errors, realistic downstream services, tests, and architecture documentation.
