# Kotlin Backend with Spring Boot - AI Agent Instructions

## Project Overview

A full-stack Kotlin/Spring Boot REST API with JWT authentication, managing users and todo items. Includes React frontends (dashboard & todo client). Architecture follows Spring Boot conventions with clear separation between controllers, services, repositories, and models.

**Key Technologies:** Kotlin 2.1, Spring Boot 3.4.1, Spring Security, JWT, JPA/Hibernate, H2 Database, Gradle

## Architecture & Data Flow

### Service Layer Pattern
The project strictly follows **Spring Boot layering** (controller → service → repository → model):
- **Controllers** ([src/main/kotlin/com/example/kotlinbackend/controller/](src/main/kotlin/com/example/kotlinbackend/controller/)): REST endpoints only, delegate all business logic to services
- **Services** ([src/main/kotlin/com/example/kotlinbackend/service/](src/main/kotlin/com/example/kotlinbackend/service/)): Core business logic, transaction management, validation
- **Repositories** ([src/main/kotlin/com/example/kotlinbackend/repository/](src/main/kotlin/com/example/kotlinbackend/repository/)): Data access via Spring Data JPA
- **Models** ([src/main/kotlin/com/example/kotlinbackend/model/](src/main/kotlin/com/example/kotlinbackend/model/)): JPA entities with Kotlin data classes

**DO NOT:** Add business logic in controllers or repositories. Always route through services.

### Authentication Flow
1. User registers/logs in via `AuthController` → `AuthService`
2. `AuthService` validates credentials and calls `JwtUtil.generateToken(user)`
3. Token returned in `AuthResponse` with user info
4. Every request carries JWT in `Authorization: Bearer <token>`
5. `JwtAuthenticationFilter` validates token and loads user via `CustomUserDetailsService`
6. Spring Security's `SecurityFilterChain` enforces authorization rules

**Key Files:**
- [src/main/kotlin/com/example/kotlinbackend/security/JwtUtil.kt](src/main/kotlin/com/example/kotlinbackend/security/JwtUtil.kt): Token generation/validation
- [src/main/kotlin/com/example/kotlinbackend/security/JwtAuthenticationFilter.kt](src/main/kotlin/com/example/kotlinbackend/security/JwtAuthenticationFilter.kt): JWT validation filter
- [src/main/kotlin/com/example/kotlinbackend/security/SecurityConfig.kt](src/main/kotlin/com/example/kotlinbackend/security/SecurityConfig.kt): Security rules & CORS

### Entity Relationships
- **User → TodoItem** (1-to-many): User has multiple todos; cascading delete removes todos when user deleted
- User implements Spring Security's `UserDetails` interface for authentication
- TodoItem includes timestamps (createdAt, updatedAt) for audit trails

## Build & Development Commands

### Gradle Commands (Kotlin Backend)
```bash
./gradlew bootRun              # Start development server (port 8080)
./gradlew clean bootRun        # Clean rebuild and run
./gradlew build                # Build production JAR
./gradlew test                 # Run unit tests
./gradlew test --info          # Tests with detailed logging
```

### Frontend Development
```bash
cd back_end_dashboard && npm run dev   # Dashboard (React, Vite)
cd todo_client && npm run dev          # Todo client (React, Vite)
```

**Database:** H2 in-memory at `jdbc:h2:mem:testdb`, console at `http://localhost:8080/h2-console`

**API Documentation:** Swagger UI at `http://localhost:8080/swagger-ui.html` (Spring Doc OpenAPI)

## Configuration & Environment Variables

Key settings in [src/main/resources/application.yml](src/main/resources/application.yml):
- **JWT Secret:** `${JWT_SECRET}` env var (fallback: hardcoded test value)
- **JWT Expiration:** `${JWT_EXPIRATION}` (default: 86400000ms = 24h)
- **CORS Origins:** `${CORS_ORIGINS}` (includes localhost:3000/4200/5173-5175, GitHub Codespaces)
- **SQL Logging:** `show-sql: true` shows all Hibernate queries (dev only)
- **DDL Strategy:** `create-drop` recreates schema on startup (perfect for dev, never use in prod)

**Profile-specific configs:**
- [application-dev.yml](src/main/resources/application-dev.yml): Development overrides
- [application-prod.yml](src/main/resources/application-prod.yml): Production settings

## Code Conventions & Patterns

### Kotlin Style
- **Data Classes:** Used for entities & DTOs (automatic `equals()`, `hashCode()`, `toString()`)
- **Immutability:** Entities use `val` fields, mutable state explicit with `mutableListOf()`
- **Type Safety:** Full type inference; avoid `!!` operator, use `?.let {}` for nulls

### DTO Pattern (CRITICAL)
- **Input DTOs:** `*Request` classes ([src/main/kotlin/com/example/kotlinbackend/dto/Dtos.kt](src/main/kotlin/com/example/kotlinbackend/dto/Dtos.kt))
- **Output DTOs:** `*Response` classes, never expose password or internal fields
- **Conversion:** `User.toUserResponse()` extension functions (don't expose raw entities)

### Entity Annotations
```kotlin
@Entity @Table(name = "users")      // JPA mapping
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
@Column(unique = true, nullable = false)  // DB constraints
@OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
```

### Service Method Pattern
```kotlin
@Service
class ExampleService(private val repo: ExampleRepository) {
    fun create(request: ExampleRequest): ExampleResponse {
        validate(request)  // Input validation
        val entity = request.toEntity()  // DTO → Entity
        val saved = repo.save(entity)
        return saved.toResponse()  // Entity → DTO
    }
}
```

## Common Workflows

### Adding a New REST Endpoint
1. Define **DTO classes** in [src/main/kotlin/com/example/kotlinbackend/dto/Dtos.kt](src/main/kotlin/com/example/kotlinbackend/dto/Dtos.kt)
2. Create **service method** with business logic in `*Service.kt`
3. Add **controller endpoint** in appropriate `*Controller.kt`, use service + return DTO
4. Add **test** in [src/test/](src/test/) following existing patterns

### Database Queries
- Use repository methods (e.g., `findByEmail()` auto-generated from field names)
- For complex queries, add `@Query` methods in repositories
- **DO NOT** use raw SQL; JPA handles dialect-agnostic queries

### Error Handling
- Services throw `RuntimeException` with descriptive messages
- Controllers catch and map to HTTP status codes (use `@ExceptionHandler` or `try-catch`)
- Avoid exposing stack traces in production responses

## Project Structure Quick Reference

```
kotlin-backend/
├── src/main/kotlin/com/example/kotlinbackend/
│   ├── KotlinBackendApplication.kt      # Main entry point
│   ├── controller/                       # REST endpoints
│   ├── service/                          # Business logic
│   ├── repository/                       # JPA data access
│   ├── model/                            # JPA entities
│   ├── dto/                              # Data transfer objects
│   ├── security/                         # JWT, CORS, auth config
│   ├── exception/                        # Custom exceptions
│   └── metrics/                          # Request logging filters
├── back_end_dashboard/                   # React admin dashboard
├── todo_client/                          # React todo app
└── build.gradle.kts                      # Gradle config (Java 22)
```

## Testing & Debugging

### Running Tests
```bash
./gradlew test                             # All tests
./gradlew test --tests AuthServiceTest    # Specific test class
```

### Debug Common Issues
- **"Unauthorized" (401):** Check JWT token validity & CORS headers in browser network tab
- **"User already exists":** Verify unique email constraint in User entity & DB
- **Port 8080 in use:** Kill process or change `server.port` in application.yml
- **H2 schema issues:** Clear build/ dir and run `./gradlew clean bootRun`

## Frontend Integration

- **Dashboard** ([back_end_dashboard/](back_end_dashboard/)): Monitoring & admin functions
- **Todo Client** ([todo_client/](todo_client/)): User-facing todo app
- Both use **Vite + React 19 + TypeScript**
- API calls target `http://localhost:8080` (dev) with JWT auth headers
- CORS configured in SecurityConfig for local ports 3000/4200/5173-5175

## Key Integration Points

- **WebSocket (optional):** `spring-boot-starter-websocket` in build.gradle; configure in SecurityConfig if needed
- **Metrics/Logging:** [RequestLogFilter](src/main/kotlin/com/example/kotlinbackend/metrics/RequestLogFilter.kt) logs all HTTP traffic
- **Validation:** Use Spring `@Valid` + Kotlin `data class` constructors for input validation
