# Project Documentation: Movie Ticket Booking App Backend

This document provides a detailed explanation of the Movie Ticket Booking App backend, including its architecture, workflow, and key components.

## 1. Project Overview

The project is a backend application for a movie ticket booking system. It provides a RESTful API to manage movie information. It's built using Kotlin and the Spring Boot framework, with a PostgreSQL database.

## 2. Technologies Used

- **Language:** Kotlin
- **Framework:** Spring Boot 3
- **Data Persistence:** Spring Data JPA, Hibernate
- **Database:** PostgreSQL
- **Database Migration:** Flyway
- **API Documentation:** SpringDoc (OpenAPI / Swagger UI)
- **Security:** Spring Security
- **DTO Mapping:** MapStruct
- **Build Tool:** Gradle

## 3. Application Architecture

The application follows a classic layered architecture, which promotes separation of concerns and maintainability:

- **Controller Layer (`controller/`):** Handles incoming HTTP requests, validates input, and delegates business logic to the service layer. It's responsible for the API endpoints.
- **Service Layer (`service/`):** Contains the core business logic of the application. It orchestrates calls to the repository layer and other services.
- **Repository Layer (`repository/`):** Responsible for data access. It uses Spring Data JPA to interact with the PostgreSQL database.
- **Model/Entity Layer (`model/`):** Defines the JPA entities that map to the database tables.
- **DTO Layer (`dto/`):** Data Transfer Objects are used to shape the data sent to and from the API, decoupling the API from the internal database structure.
- **Mapper Layer (`mapper/`):** Uses MapStruct to automatically generate boilerplate code for mapping between Entities and DTOs.
- **Configuration (`config/`):** Contains configuration classes for aspects like Security, OpenAPI, etc.

## 4. Database Schema

The database schema is managed by Flyway using SQL migration scripts located in `src/main/resources/db/migration`.

- **`movies`**: The central table storing all movie details like title, synopsis, release date, status, etc.
- **`genres`**: Stores movie genres (e.g., Action, Comedy).
- **`formats`**: Stores movie formats (e.g., 2D, 3D, IMAX).
- **`movie_genres`**: A junction table to manage the many-to-many relationship between movies and genres.
- **`movie_formats`**: A junction table to manage the many-to-many relationship between movies and formats.

The schema also includes indexes for performance and a trigger (`set_updated_at`) to automatically update the `updated_at` timestamp on movies.

## 5. Core Components & Workflow

### Workflow of a Typical API Request

Here is a step-by-step example for a `GET /api/v1/movies/{id}` request:

1.  **HTTP Request:** A client sends a `GET` request to `/api/v1/movies/1` to fetch the movie with ID 1.
2.  **Security:** Spring Security intercepts the request. Based on `SecurityConfig.kt`, this endpoint is public (`permitAll`), so the request is allowed.
3.  **DispatcherServlet:** Spring's front controller routes the request to the appropriate method in `MovieController.kt`.
4.  **Controller (`MovieController`):** The `getMovieById` method is invoked. It calls the `MovieService.getMovieById()` method, passing the ID.
5.  **Service (`MovieService`):** The `getMovieById` method calls the `MovieRepository.findById()`.
6.  **Repository (`MovieRepository`):** Spring Data JPA generates the SQL query to select the movie from the `movies` table where the `id` matches.
7.  **Database:** The query is executed on the PostgreSQL database, and the movie record is returned.
8.  **Entity (`Movie`):** JPA maps the returned record to a `Movie` entity object.
9.  **Service (cont.):** The service receives the `Movie` entity. It then uses the `MovieMapper` to convert the `Movie` entity into a `MovieDto` object. This is crucial for separating the API representation from the database entity.
10. **Controller (cont.):** The controller receives the `MovieDto` from the service.
11. **HTTP Response:** Spring Boot, using Jackson, serializes the `MovieDto` object into a JSON string and sends it back to the client with a `200 OK` status code.

### Code Deep Dive

#### `MovieController.kt`

- **Purpose:** Defines the REST API endpoints for movies.
- **Path:** All endpoints are prefixed with `/api/v1/movies`.
- **Key Methods & Return Values:**
    - `getAllMovies()`:
        - **Endpoint:** `GET /`
        - **Description:** Retrieves a list of all movies.
        - **Returns:** `ResponseEntity<List<MovieDto>>` - A list of movie DTOs.
    - `getMovieById(id: Long)`:
        - **Endpoint:** `GET /{id}`
        - **Description:** Retrieves a single movie by its ID.
        - **Returns:** `ResponseEntity<MovieDto>` - The movie DTO if found, otherwise likely a 404 Not Found (handled by an exception handler).
    - `addMovie(movieDto: MovieDto)`:
        - **Endpoint:** `POST /`
        - **Description:** Adds a new movie.
        - **Returns:** `ResponseEntity<MovieDto>` - The newly created movie DTO with its generated ID.
    - `updateMovie(id: Long, movieDto: MovieDto)`:
        - **Endpoint:** `PUT /{id}`
        - **Description:** Updates an existing movie.
        - **Returns:** `ResponseEntity<MovieDto>` - The updated movie DTO.
    - `deleteMovie(id: Long)`:
        - **Endpoint:** `DELETE /{id}`
        - **Description:** Deletes a movie by its ID.
        - **Returns:** `ResponseEntity<Void>` - An empty response with a 204 No Content status.

#### `MovieService.kt`

- **Purpose:** Implements the business logic for movie operations.
- **Key Methods & Return Values:**
    - `getAllMovies()`:
        - **Logic:** Fetches all `Movie` entities from the repository and maps them to a list of `MovieDto`s using `MovieMapper`.
        - **Returns:** `List<MovieDto>`
    - `getMovieById(id: Long)`:
        - **Logic:** Fetches a `Movie` by its ID. If not found, it throws an exception. Otherwise, it maps the entity to a `MovieDto`.
        - **Returns:** `MovieDto`
    - `addMovie(movieDto: MovieDto)`:
        - **Logic:** Maps the incoming `MovieDto` to a `Movie` entity, saves it using the repository, and then maps the saved entity (with its new ID) back to a `MovieDto`.
        - **Returns:** `MovieDto`
    - `updateMovie(id: Long, movieDto: MovieDto)`:
        - **Logic:** Fetches the existing movie, updates its properties from the DTO, saves the updated entity, and maps it back to a DTO.
        - **Returns:** `MovieDto`
    - `deleteMovie(id: Long)`:
        - **Logic:** Calls the repository's `deleteById` method.
        - **Returns:** `Unit` (void)

#### `MovieRepository.kt`

- **Purpose:** Data access layer for the `Movie` entity.
- **Implementation:** It's an interface that extends `JpaRepository<Movie, Long>`.
- **Functionality:** Spring Data JPA automatically provides standard CRUD (Create, Read, Update, Delete) methods like `findAll()`, `findById()`, `save()`, `deleteById()`, etc. No custom methods are defined yet, but they could be added here (e.g., `findByTitle(title: String)`).

#### `model/Movie.kt`

- **Purpose:** Defines the `Movie` entity, which maps directly to the `movies` table in the database.
- **Annotations:**
    - `@Entity`: Marks this class as a JPA entity.
    - `@Table(name = "movies")`: Specifies the table name.
    - `@Id`, `@GeneratedValue`: Defines the primary key and its generation strategy.
    - `@Column`: Maps fields to table columns.
    - `@ManyToMany`: Defines the many-to-many relationships with `Genre` and `Format` entities, specifying the junction tables (`movie_genres`, `movie_formats`).

#### `dto/MovieDto.kt`

- **Purpose:** A Data Transfer Object used to transfer movie data between the client and the server. It helps to hide internal entity details and shape the API response.
- **Fields:** It contains a subset of the fields from the `Movie` entity, often simplified (e.g., using lists of strings for genres and formats instead of the full objects).

#### `mapper/MovieMapper.kt`

- **Purpose:** An interface that defines the mapping between `Movie` entities and `MovieDto`s.
- **Implementation:** Uses MapStruct (`@Mapper(componentModel = "spring")`). The actual implementation of this interface is generated automatically by the MapStruct processor during the build process, saving a lot of manual boilerplate code.

#### `config/SecurityConfig.kt`

- **Purpose:** Configures the application's security rules.
- **Key Configuration:**
    - It defines a `SecurityFilterChain` bean.
    - `csrf().disable()`: Disables Cross-Site Request Forgery protection, common for stateless REST APIs.
    - `authorizeHttpRequests`: Configures which endpoints require authentication. In the current setup, it permits all requests (`.requestMatchers("/**").permitAll()`), effectively making the API public. This could be changed to secure certain endpoints (e.g., `POST`, `PUT`, `DELETE`).
