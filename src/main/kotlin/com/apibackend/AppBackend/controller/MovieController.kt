package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.service.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class MovieController(private val movieService: MovieService) {

    @GetMapping("/movies")
    fun getAllMovies(
            @RequestParam(required = false) status: MovieStatus?
    ): ResponseEntity<List<MovieDto>> {
        val movies =
                if (status != null) {
                    movieService.getMoviesByStatus(status)
                } else {
                    movieService.getAllActiveMovies()
                }
        return ResponseEntity.ok(movies)
    }
}
