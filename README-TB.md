# TB - Spring Boot 3.4.5 skeleton

## Build
```bash
./mvnw clean package -DskipTests
```

## Docker
```bash
docker compose up -d --build
# App: http://localhost:8080/health
```

## Packages
- boot: entrypoint
- api: REST controllers, DTOs, middleware
- application: use cases, services, DTO internos
- domain: modelos, VOs, policies, ports
- infrastructure: adapters (jdbc, geo, security), configs
- shared: utilidades y errores
