# API Gateway with JWT Auth & Redis Rate Limiting (Spring Cloud Gateway)

This repository contains a minimal multi-service example:
- **api-gateway** — Spring Cloud Gateway with JWT global auth filter, Redis-backed rate limiting, Resilience4j circuit breakers and simple fallback endpoints.
- **product-service** — simple downstream service exposing `/products/**`.
- **user-service** — simple downstream service exposing `/users/**`.
- **redis** — Redis used by the gateway rate limiter.

## Quick start (using Docker Compose)

1. Build the Maven artifacts and Docker images (from project root)

```bash
# build each service jar
cd api-gateway
mvn -DskipTests package
cd ../product-service
mvn -DskipTests package
cd ../user-service
mvn -DskipTests package
cd ..

# then start using docker-compose
docker compose up --build
```

2. Get a token (optional helper):
```bash
curl -s -X POST http://localhost:8080/auth/token -H "Content-Type: application/json" -d '{"userId":"user123","roles":["USER"]}' | jq
```

3. Call endpoints through gateway:
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/products
curl -H "Authorization: Bearer <token>" http://localhost:8080/users/me
```

## Notes, troubleshooting & customization

- **Secret key**: The JWT secret in `JwtUtil` is a placeholder. Replace it with a secure secret (and preferably load from env/secret manager).
- **Versions**: The project uses Spring Boot 3.x and Spring Cloud 2023.x BOM. If you run into dependency issues, update versions in the `pom.xml` files to match your local environment.
- **Rate limiting**: Configured per-route in `application.yml` using `RequestRateLimiter` backed by Redis.
- **RBAC checks**: This example forwards `X-User-Roles` to downstream services. For stricter RBAC, apply checks in the gateway filter or use an authorization server.
- **Production**: For production use, add HTTPS, stronger secret management, token revocation, monitoring and observability.

