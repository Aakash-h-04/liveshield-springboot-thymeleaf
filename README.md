# LiveShield — Farm Biosecurity Companion

Spring Boot + Thymeleaf + PostgreSQL.

## Requirements
- Java 21+
- Maven 3.9+
- Docker Desktop (recommended for PostgreSQL)

## Run PostgreSQL
```bash
docker compose up -d
```

## Run application
```bash
./mvnw spring-boot:run
```
Or with Maven installed:
```bash
mvn spring-boot:run
```

Open http://localhost:8080

## API
- GET `/api/farms`
- GET `/api/farms/{id}`
- POST `/api/farms`
- PUT `/api/farms/{id}`
- DELETE `/api/farms/{id}`

The application uses PostgreSQL persistence through Spring Data JPA/Hibernate. Tables are created/updated automatically during development.
