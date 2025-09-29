package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.dto.CreateMovieDto
import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.dto.UpdateMovieDto
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.service.MovieService
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
    fun createMovie(@RequestBody createDto: CreateMovieDto): ResponseEntity<MovieDto> {
        val createdMovie = movieService.createMovie(createDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie)
    }

    @PutMapping("/{id}")
    fun updateMovie(
            @PathVariable id: Long,
            @RequestBody updateDto: UpdateMovieDto
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
