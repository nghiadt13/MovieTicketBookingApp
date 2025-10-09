package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.config.ApiError
import com.apibackend.AppBackend.dto.CreateMovieDto
import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.dto.UpdateMovieDto
import jakarta.validation.Valid
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.service.MovieService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "Movie catalogue endpoints: list, paged, get, create, update, soft delete.")
class MovieController(private val movieService: MovieService) {

    @GetMapping
    @Operation(
        summary = "List movies",
        description = "Returns movies filtered by optional status. If status is omitted and activeOnly=true (default) returns only active movies; otherwise returns all."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful retrieval",
                content = [Content(array = ArraySchema(schema = Schema(implementation = MovieDto::class)))]
            )
        ]
    )
    fun getAllMovies(
            @Parameter(description = "Filter by movie status") @RequestParam(required = false) status: MovieStatus?,
            @Parameter(description = "Return only active movies (soft-delete hidden)") @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean
    ): ResponseEntity<List<MovieDto>> {
        val movies =
                when {
                    status != null -> movieService.getMoviesByStatus(status)
                    activeOnly -> movieService.getAllActiveMovies()
                    else -> movieService.getAllMovies()
                }
        return ResponseEntity.ok(movies)
    }

    @GetMapping("/paged")
    @Operation(
        summary = "List movies (paged)",
        description = "Standard Spring pagination: `page` (0-based), `size`, `sort` (e.g., `createdAt,desc`). Hydrates genres and formats per page efficiently."
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successful retrieval")])
    fun getMoviesPaged(
            @Parameter(description = "Filter by movie status") @RequestParam(required = false) status: MovieStatus?,
            @Parameter(description = "Return only active movies (soft-delete hidden)") @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean,
            @ParameterObject @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<MovieDto>> {
        val page = movieService.getMoviesPaged(status, activeOnly, pageable)
        return ResponseEntity.ok(page)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie by id", description = "Returns a single movie by id. Currently returns inactive movies as well.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found", content = [Content(schema = Schema(implementation = MovieDto::class))]),
            ApiResponse(responseCode = "404", description = "Not found")
        ]
    )
    fun getMovieById(@PathVariable id: Long): ResponseEntity<MovieDto> {
        val movie = movieService.getMovieById(id)
        return if (movie != null) {
            ResponseEntity.ok(movie)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    @Operation(summary = "Create movie", description = "Creates a new movie and associates provided genres/formats by ids.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Created", content = [Content(schema = Schema(implementation = MovieDto::class))]),
            ApiResponse(responseCode = "400", description = "Validation error", content = [Content(schema = Schema(implementation = ApiError::class))])
        ]
    )
    fun createMovie(@Valid @RequestBody createDto: CreateMovieDto): ResponseEntity<MovieDto> {
        val createdMovie = movieService.createMovie(createDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update movie", description = "Updates mutable fields and optionally re-associates genres/formats.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Updated", content = [Content(schema = Schema(implementation = MovieDto::class))]),
            ApiResponse(responseCode = "400", description = "Validation error", content = [Content(schema = Schema(implementation = ApiError::class))]),
            ApiResponse(responseCode = "404", description = "Not found")
        ]
    )
    fun updateMovie(
            @PathVariable id: Long,
            @Valid @RequestBody updateDto: UpdateMovieDto
    ): ResponseEntity<MovieDto> {
        val updatedMovie = movieService.updateMovie(id, updateDto)
        return if (updatedMovie != null) {
            ResponseEntity.ok(updatedMovie)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete movie", description = "Marks the movie as inactive; data remains for auditing and history.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Deleted"),
        ApiResponse(responseCode = "404", description = "Not found")
    ])
    fun deleteMovie(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = movieService.deleteMovie(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
