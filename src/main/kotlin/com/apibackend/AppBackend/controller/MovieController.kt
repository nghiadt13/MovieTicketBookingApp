package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.dto.CreateMovieDto
import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.dto.UpdateMovieDto
import jakarta.validation.Valid
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/movies")
class MovieController(private val movieService: MovieService) {

    @GetMapping
    fun getAllMovies(
            @RequestParam(required = false) status: MovieStatus?,
            @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean
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
    fun getMoviesPaged(
            @RequestParam(required = false) status: MovieStatus?,
            @RequestParam(required = false, defaultValue = "true") activeOnly: Boolean,
            @PageableDefault(size = 20, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<MovieDto>> {
        val page = movieService.getMoviesPaged(status, activeOnly, pageable)
        return ResponseEntity.ok(page)
    }

    @GetMapping("/{id}")
    fun getMovieById(@PathVariable id: Long): ResponseEntity<MovieDto> {
        val movie = movieService.getMovieById(id)
        return if (movie != null) {
            ResponseEntity.ok(movie)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createMovie(@Valid @RequestBody createDto: CreateMovieDto): ResponseEntity<MovieDto> {
        val createdMovie = movieService.createMovie(createDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie)
    }

    @PutMapping("/{id}")
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
    fun deleteMovie(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = movieService.deleteMovie(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
