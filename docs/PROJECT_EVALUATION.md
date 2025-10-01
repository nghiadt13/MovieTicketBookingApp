# Project Evaluation – AppBackend (MovieTicketBookingApp)

## Overview

- Stack: Kotlin, Spring Boot 3.5 (Web, Data JPA, Security), MapStruct, Flyway, PostgreSQL, Gradle Kotlin DSL, Java 17.
- Domain: Movies with genres and formats, including soft-delete via `is_active`.
- Build: Gradle wrapper present; compiles cleanly; Flyway migration V1 defines full schema and enum.

## Strengths

- Clear layering: controller → service → repository → entity/DTO/mapper.
- Database migration with Flyway and a well-thought schema (constraints, indexes, triggers).
- DTOs and MapStruct for separation of concerns between API and persistence.
- Transactional service methods; soft delete is respected by repository methods for “active” reads.
- Security is in place; `/api/movies/**` public, others require auth (stubbed for future).

## Key Findings

- NPE bug fixed: MapStruct passed nulls to non-null Kotlin fields (`ratingAvg`, timestamps, collections). Mappings now set safe defaults and timestamps.
- Multiple collection fetch-joins: queries fetch-join `genres` and `formats`. This can produce large Cartesian products and duplicates without `DISTINCT`, and may stress Hibernate. Consider entity graphs or batching.
- Potential N+1: `getAllMovies()` uses `findAll()` without fetch joins; mapping to DTO will lazily load `genres`/`formats` per entity.
- Entities as Kotlin data classes:
  - Data classes auto-generate `equals/hashCode` with all fields, which is often not recommended for JPA entities (identity can change pre/post persist). Consider custom `equals/hashCode` based on identifier or avoid data classes for entities.
- Dual timestamp management: DB trigger updates `updated_at`, and application sets timestamps, risking mismatches. Prefer one source of truth (DB triggers or JPA callbacks `@PrePersist/@PreUpdate`).
- Validation missing: Controller does not use `@Valid`; DTOs lack Bean Validation annotations (e.g., `@NotBlank`, `@Size`, `@Positive`).
- Pagination missing: `GET /api/movies` returns full list; no `Pageable`/`Sort` support.
- CORS/security config:
  - Uses both `allowedOrigins` and wildcard `allowedOriginPatterns` `*`; duplication can be simplified. If credentials are needed, set `allowCredentials=true` and avoid `*`.
  - CSRF disabled globally, which is fine for stateless APIs; document rationale.
- Configuration/ops:
  - `application.properties` contains credentials and shows encoding artifacts; move secrets to env/profiles and fix file encoding to UTF‑8.
  - Logging is verbose for SQL (DEBUG/TRACE). Good for dev; reduce for prod.
- Mapper/API consistency:
  - `updateMovieFromDto` in `MovieMapper` is a no-op; service uses `.copy(...)`. Remove or implement for clarity.
  - `MovieMapper` declares a companion `INSTANCE` even though `componentModel = spring` provides a bean—`INSTANCE` is redundant.
- Repository query semantics:
  - Add `DISTINCT` to fetch-join queries to reduce duplicates.
  - `findByIdWithGenresAndFormats` returns inactive movies too; decide if the API should hide inactive on GET by id.

## Recommendations

1. Mapping and Entities
   - Keep mapper defaults we added (non-null defaults). Remove `INSTANCE` field; use Spring injection only.
   - Consider replacing data classes for entities or override `equals/hashCode` based on `id` only.
   - Consolidate timestamp handling using JPA callbacks or DB triggers exclusively.

2. Repositories and Performance
   - Add `DISTINCT` to JPQL with multiple fetch-joins.
   - Consider `@EntityGraph` for read queries, or projection DTO queries.
   - Avoid multiple collection fetch-joins for wide lists; use pagination + batch fetching (`@BatchSize`) or `FetchMode.SUBSELECT`.
   - Ensure `getAllMovies()` uses an optimized query (fetch-join or projection) to avoid N+1.

3. API Design
   - Add pagination and sorting using `Pageable` for `GET /api/movies`.
   - Add validation: annotate DTOs (e.g., `@NotBlank title`, `@Positive durationMin`) and use `@Valid` in controller.
   - Provide consistent 404/400 error responses via `@ControllerAdvice` with structured error body.
   - Consider endpoints to manage `genres` and `formats` (CRUD or read-only lists).

4. Security & CORS
   - Tighten CORS: prefer explicit origins per environment. If credentials are needed, set `allowCredentials=true` and remove `*` patterns.
   - Add authentication (e.g., JWT) if endpoints beyond movies are added; keep `/api/movies/**` public if intended.

5. Build, Test, and Ops
   - Add tests:
     - Repository tests with Testcontainers Postgres validating fetch-joins and filtering logic.
     - Controller tests (MockMvc/WebTestClient) and service tests.
   - Add OpenAPI (springdoc) for API docs and Swagger UI exposure in dev profile.
   - Add profiles (`application-local`, `application-prod`) and move secrets to environment variables.
   - Add Docker support: Dockerfile + docker-compose for Postgres; or use Spring Boot Buildpacks.
   - Add static analysis/formatting: Spotless (ktlint) and Detekt.
   - Reduce SQL logging in prod; keep in dev profile.
   - Add CI (GitHub Actions) to build, test, run lints.

## Quick Wins

- Add `DISTINCT` to fetch-join queries and add `@EntityGraph` where suitable.
- Add DTO validation + `@Valid` in controller.
- Replace `findAll()` path with a fetch-join/projection method to avoid N+1.
- Remove `MovieMapper.INSTANCE` and implement or remove `updateMovieFromDto`.
- Fix `application.properties` encoding; externalize DB credentials.
- Add Swagger UI and basic README usage instructions.

## Longer-Term

- Revisit entity equality strategy; consider non-data classes for JPA entities.
- Introduce pagination across list endpoints.
- Consolidate timestamp/source-of-truth and unify soft-delete semantics across queries.
- Add test coverage with Testcontainers and CI.
- Add authentication/authorization when expanding the domain.

## Run & Test Notes

- Local DB required by default: `jdbc:postgresql://localhost:5432/MobileApp` (user `postgres`, password `123`). Prefer an `application-local.properties` and environment variables.
- Build: `./gradlew build` (tests to be added). Run: `./gradlew bootRun`.

---

Last reviewed: current repo state after fixing the MapStruct null-initialization bug in `createDtoToMovie`.
