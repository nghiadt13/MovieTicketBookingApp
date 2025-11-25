package com.apibackend.AppBackend.homepage.controller

import com.apibackend.AppBackend.homepage.dto.CinemaDto
import com.apibackend.AppBackend.homepage.service.CinemaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cinemas")
@Tag(name = "Cinemas", description = "Cinema endpoints: list cinemas showing a specific movie")
class CinemaController(private val cinemaService: CinemaService) {

        @GetMapping
        @Operation(
                summary = "Get cinemas by movie",
                description =
                        "Returns all active cinemas that are currently showing the specified movie. " +
                                "Used when user clicks 'Book Ticket' from movie detail page."
        )
        @ApiResponses(
                value =
                        [
                                ApiResponse(
                                        responseCode = "200",
                                        description = "Successful retrieval",
                                        content =
                                                [
                                                        Content(
                                                                array =
                                                                        ArraySchema(
                                                                                schema =
                                                                                        Schema(
                                                                                                implementation =
                                                                                                        CinemaDto::class
                                                                                        )
                                                                        )
                                                        )]
                                ),
                                ApiResponse(
                                        responseCode = "400",
                                        description = "Movie ID is required"
                                )]
        )
        fun getCinemasByMovie(
                @Parameter(
                        description = "Movie ID (required) - ID of the movie to find cinemas for",
                        required = true
                )
                @RequestParam
                movieId: Long,
                @Parameter(description = "Filter by city name (optional)")
                @RequestParam(required = false)
                city: String?
        ): ResponseEntity<List<CinemaDto>> {
                val cinemas = cinemaService.getCinemasByMovieId(movieId)

                val filteredCinemas =
                        if (city != null) {
                                cinemas.filter { it.city.equals(city, ignoreCase = true) }
                        } else {
                                cinemas
                        }

                return ResponseEntity.ok(filteredCinemas)
        }
}
