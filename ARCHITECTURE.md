# Architecture

## High-level architecture

LIFE QUEST backend follows a layered Spring Boot architecture:

- **API Layer** (`com.lifequest.api`): REST controllers, DTOs, and exception advice.
- **Application Layer** (`com.lifequest.application`): business services and domain workflows.
- **Domain Layer** (`com.lifequest.domain`): JPA entities, enums, and repositories.
- **Infrastructure Layer** (`com.lifequest.infrastructure`): security, caching, and messaging integrations.
- **Configuration** (`com.lifequest.config`): Spring Boot configuration bindings.

## Package structure & responsibilities

- `com.lifequest.api.controller`
  - Defines REST endpoints for auth, users, quests.
- `com.lifequest.api.dto`
  - Request/response DTOs and response wrappers.
- `com.lifequest.api.advice`
  - Centralized exception handling and error codes.
- `com.lifequest.application.service`
  - Business logic (auth, user profile, quest completion workflow).
- `com.lifequest.application.event`
  - Placeholder for domain events (e.g., Kafka).
- `com.lifequest.domain.user`
  - User entity, stats, role enum, repositories.
- `com.lifequest.domain.quest`
  - Quest entities, status/type enums, repositories.
- `com.lifequest.domain.guild`
  - Guild entity and repository.
- `com.lifequest.domain.item`
  - Item entities and enums.
- `com.lifequest.infrastructure.security`
  - JWT handling, auth filter, security config, refresh token storage.
- `com.lifequest.infrastructure.cache`
  - Placeholder for Redis configuration.
- `com.lifequest.infrastructure.messaging`
  - Placeholder for Kafka configuration.
- `com.lifequest.config`
  - Configuration properties bindings (e.g., JWT).

## Domain model overview

Entities and relationships (based on JPA mappings):

- **User**
  - One-to-one with **UserStats**.
  - Many-to-one with **Guild**.
  - Owns multiple **UserQuest** and **UserItem** records.
- **UserStats**
  - Stores six stat values for a single user.
- **Quest**
  - Quest template metadata (type, difficulty, base XP).
  - Created by a **User**.
- **UserQuest**
  - Join entity between **User** and **Quest**.
  - Tracks status, completion, and earned rewards.
- **Guild**
  - Guild data, references a master **User**.
- **Item**
  - Item template metadata.
- **UserItem**
  - Join entity between **User** and **Item**.

## Request flow

Typical API flow:

1. **Controller** receives HTTP request and validates DTOs.
2. **Service** executes business logic (e.g., quest completion, XP calculation).
3. **Repository** queries/persists entities via JPA.
4. **Database** (PostgreSQL) stores persistent state.

Authentication requests pass through `JwtAuthenticationFilter` before controllers.

## Transaction boundaries

- Service methods that modify state are annotated with `@Transactional`.
- Read-only methods are typically non-transactional.
- Quest completion is a single transaction to ensure XP, gold, stats, and quest status update atomically.

## Key configurations

- **Security**
  - `SecurityConfig` defines stateless JWT-based authentication and authorization.
  - `JwtAuthenticationFilter` parses and validates the bearer token.
- **Exception handling**
  - `GlobalExceptionHandler` converts exceptions to the standard API error format.
- **Swagger**
  - Not configured yet; recommended to add Springdoc for API docs.

## Where to add new features safely

- **New API endpoints**: add controller classes under `com.lifequest.api.controller` and DTOs under `com.lifequest.api.dto`.
- **Business logic**: add/extend services in `com.lifequest.application.service`.
- **New domain concepts**: create entities and repositories under `com.lifequest.domain`.
- **Infrastructure concerns**: add configuration in `com.lifequest.infrastructure` (Redis caching, Kafka messaging, etc.).
- **Cross-cutting concerns**: handle errors in `api.advice` and security in `infrastructure.security`.
