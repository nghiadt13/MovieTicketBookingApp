# Project Explanation v1 – AppBackend (MovieTicketBookingApp)

This document explains the project’s architecture, request workflow, data model, key components, and how the major functions work end‑to‑end. Use it as a guide for development, debugging, and onboarding.

## Tech Stack

- Language/Runtime: Kotlin (JDK 17)
- Framework: Spring Boot 3.5 (Web, Data JPA, Security, Validation)
- Persistence: JPA/Hibernate with PostgreSQL
- Migrations: Flyway (`db/migration/V1__Create_movies_schema.sql`)
- Mapping: MapStruct (DTO ↔ Entity)
- Build: Gradle Kotlin DSL (Gradle wrapper included)

## High‑Level Architecture

- Controller layer (REST): Validates input, exposes HTTP endpoints, returns DTOs.
- Service layer (business): Transactional business logic; orchestrates repositories and mappings.
- Repository layer (data access): Spring Data JPA repositories.
- Domain model: JPA entities (`Movie`, `Genre`, `Format`) + enum (`MovieStatus`).
- DTO layer: API data contracts.
- Mapper: `MovieMapper` generates implementations for DTO ↔ Entity conversions.
- Config: Security, validation, CORS, global exception handling.

## Request Lifecycle (Typical)

1. Client sends HTTP request to a controller endpoint.
2. Spring validates request body/params (Bean Validation + `@Valid`).
3. Controller calls Service, passing validated DTO/params.
4. Service queries Repositories (optionally paged), then hydrates associations.
5. Service maps entities to DTOs using MapStruct.
6. Controller returns `ResponseEntity` with DTO(s) and appropriate status code.

## Database Schema (Flyway V1)

- `movies` (core): fields include `title`, `synopsis`, `duration_min`, `release_date`, `status` (enum), `poster_url`, `trailer_url`, `rating_avg`, `rating_count`, `is_active`, `created_at`, `updated_at`.
- `genres` (lookup): unique `name`, unique `slug` (CITEXT).
- `formats` (lookup): unique `code`.
- `movie_genres` and `movie_formats`: N–N join tables.
- Triggers/Function: updates `updated_at` on `movies` row update.
- Indexes: status, is_active (partial), join table FKs.

## Entities

- `Movie` (data class)
  - Many‑to‑many with `Genre` and with `Format`.
  - Enum `MovieStatus` mapped using `@JdbcType(PostgreSQLEnumJdbcType::class)`.
  - Immutable style via Kotlin `data class` + `.copy(...)` for updates.
- `Genre` / `Format`: simple lookup entities.

Note: Kotlin data classes generate `equals/hashCode` including all properties. For JPA, you may later prefer custom equality based on `id` only.

## DTOs and Validation

- `MovieDto`: Read model sent to clients.
- `CreateMovieDto`:
  - `title`: `@NotBlank`
  - `durationMin`: `@Positive` (if present)
  - `genreIds`, `formatIds`: lists of ids to associate.
- `UpdateMovieDto`:
  - `title`: `@Size(min=1)` (if provided)
  - `durationMin`: `@Positive` (if provided)

Validation errors are handled by `GlobalExceptionHandler` and returned as structured JSON.

## Mapping (MapStruct)

- `MovieMapper#createDtoToMovie` sets safe defaults to prevent NPEs:
  - `ratingAvg = BigDecimal.ZERO`, `ratingCount = 0`
  - `active = true`
  - `createdAt = now()`, `updatedAt = now()`
  - `genres = emptySet`, `formats = emptySet`
- Mapping to DTO converts entity collections to lists of lightweight DTOs.

## Repositories

- `MovieRepository` extends `JpaRepository<Movie, Long>` and provides:
  - Filtered lists: `findByActiveTrue()`, `findByActiveTrueAndStatus(...)`.
  - Paged variants: `findByActiveTrue(pageable)`, `findByActiveTrueAndStatus(status, pageable)`, `findByStatus(status, pageable)`.
  - Fetch‑join lists with `DISTINCT`: `findAllActiveWithGenresAndFormats()`, `findByStatusWithGenresAndFormats(...)`, `findByIdWithGenresAndFormats(id)`.
  - Hydration helper: `@EntityGraph(attributePaths = ["genres", "formats"]) fun findByIdIn(ids: List<Long>): List<Movie>` to load associations in bulk for a page.

Rationale: Multi‑collection fetch‑joins plus pagination are tricky; we page base rows first, then hydrate.

## Services – How Functions Work

- `getAllActiveMovies()`
  - Calls `findAllActiveWithGenresAndFormats()` to fetch active movies with genres and formats loaded.
  - Maps to `MovieDto` list.

- `getMoviesByStatus(status)`
  - Uses `findByStatusWithGenresAndFormats(status)` to fetch/hydrate and returns mapped DTOs.

- `getMovieById(id)`
  - Calls `findByIdWithGenresAndFormats(id)`; returns `null` if not found; maps if present.
  - Note: returns inactive movies as well; adjust repo if you want to hide inactive by id.

- `createMovie(createDto)`
  - Validates input (controller).
  - Loads `Genre` and `Format` by `genreIds`/`formatIds`.
  - Maps DTO → `Movie` using MapStruct defaults.
  - Replaces associations with loaded sets; ensures timestamps; saves.
  - Maps saved entity → `MovieDto` and returns 201.

- `updateMovie(id, updateDto)`
  - Finds existing movie; returns 404 if not found.
  - Determines new associations if ids provided, else keeps existing.
  - Uses `.copy(...)` to update fields; sets `updatedAt = now()`; saves.
  - Maps to and returns `MovieDto`.

- `deleteMovie(id)` (soft delete)
  - Finds movie; if not found returns 404.
  - Sets `active = false`, updates `updatedAt`; saves; returns 204.

- `getAllMovies()`
  - Returns all movies with associations via `findAllWithGenresAndFormats()`; maps to DTO list.

- `getMoviesPaged(status, activeOnly, pageable)` – Pagination Workflow
  1. Page base rows using repository page methods (no fetch‑join):
     - If `status != null` and `activeOnly`: `findByActiveTrueAndStatus(status, pageable)`.
     - If `status != null` and not `activeOnly`: `findByStatus(status, pageable)`.
     - If only `activeOnly`: `findByActiveTrue(pageable)`.
     - Else: `findAll(pageable)`.
  2. Hydrate associations for the page’s ids using `findByIdIn(ids)` with `@EntityGraph`.
  3. Preserve page order; map to DTO list.
  4. Return `PageImpl<MovieDto>` with original `totalElements` and pageable.

## Controllers – Endpoints

Base path: `/api/movies`

- `GET /api/movies` – list (non‑paged)
  - Query: `status` (optional), `activeOnly` (default `true`)
  - Logic: if `status` set → by status; else if `activeOnly` → active only; else → all
  - Response: `200 OK` `[MovieDto]`

- `GET /api/movies/paged` – paged list
  - Query: `status` (optional), `activeOnly` (default `true`), `page`, `size`, `sort`
  - Default sort: `createdAt,DESC`
  - Response: `200 OK` `Page<MovieDto>` (Spring Page JSON structure)

- `GET /api/movies/{id}` – get one by id
  - Response: `200 OK` `MovieDto` or `404 Not Found`
  - Note: returns inactive movies too.

- `POST /api/movies` – create
  - Body: `CreateMovieDto`
  - Validation: `title` required; positive `durationMin` if provided
  - Response: `201 Created` with created `MovieDto`

- `PUT /api/movies/{id}` – update
  - Body: `UpdateMovieDto`
  - Validation: if `title` provided, must be non‑empty; positive `durationMin` if provided
  - Response: `200 OK` with updated `MovieDto` or `404 Not Found`

- `DELETE /api/movies/{id}` – soft delete
  - Response: `204 No Content` or `404 Not Found`

## Security and CORS

- Public routes: `/api/movies/**`.
- Other routes require authentication (auth not implemented yet).
- CORS allows local dev origins (3000, 5173) and credentials; wildcard patterns are not used.

## Error Handling

- `GlobalExceptionHandler` returns consistent JSON with fields: `timestamp`, `status`, `error`, `message`, `path`, `fieldErrors` (if validation).
- Examples:
  - 400 on validation failure (`MethodArgumentNotValidException`).
  - 400 on unreadable JSON (`HttpMessageNotReadableException`).
  - 500 for unexpected exceptions.

## Configuration

- `src/main/resources/application.properties`
  - DB via env overrides: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
  - JPA: `ddl-auto=none` (Flyway controls DDL), `open-in-view=false`.
  - SQL logging enabled for development.

## Build & Run

- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- DB: PostgreSQL accessible at `${DB_URL:jdbc:postgresql://localhost:5432/MobileApp}` (default user `postgres`, pass `123`).

## Example Requests

Create movie

```bash
curl -X POST http://localhost:8080/api/movies \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Interstellar",
    "synopsis": "Explorers travel through a wormhole.",
    "durationMin": 169,
    "releaseDate": "2014-11-07",
    "status": "NOW_SHOWING",
    "posterUrl": "https://.../poster.jpg",
    "trailerUrl": "https://.../trailer.mp4",
    "genreIds": [1,2],
    "formatIds": [1]
  }'
```

Paged list (page 0, size 20, newest first by `createdAt`)

```bash
curl 'http://localhost:8080/api/movies/paged?page=0&size=20&sort=createdAt,desc'
```

Update movie

```bash
curl -X PUT http://localhost:8080/api/movies/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "title": "Interstellar (Remastered)",
    "formatIds": [1,2]
  }'
```

Soft delete

```bash
curl -X DELETE http://localhost:8080/api/movies/1 -i
```

## Operational Notes

- Soft delete is implemented via `is_active` (entity field `active`).
  - List endpoints respect `activeOnly` by default.
  - `GET /api/movies/{id}` currently returns inactive items as well; change the repo to filter if desired.
- Timestamps are set by both DB and application; choose one source of truth if strict consistency is needed.

## Limitations & Future Improvements

- Entities are Kotlin data classes; consider custom equality or non‑data classes for JPA best practices.
- Add OpenAPI/Swagger (springdoc) for API docs.
- Add Testcontainers tests and CI workflows.
- Consider projection DTO queries for large lists and to avoid multiple collection fetch joins.
- Add endpoints to manage `genres` and `formats` (seed/read or CRUD).

---

Last updated: current repository state after pagination and mapping fixes.
