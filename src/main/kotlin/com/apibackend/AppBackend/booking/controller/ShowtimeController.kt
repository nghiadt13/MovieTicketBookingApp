package com.apibackend.AppBackend.booking.controller

import com.apibackend.AppBackend.booking.dto.ShowtimeDetailDto
import com.apibackend.AppBackend.booking.service.ShowtimeService
import com.apibackend.AppBackend.common.config.ApiError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/booking/showtimes")
@Tag(name = "Booking - Showtimes", description = "Showtime endpoints for booking flow")
class ShowtimeController(private val showtimeService: ShowtimeService) {

    @GetMapping("/{id}")
    @Operation(
            summary = "Get showtime detail",
            description =
                    """
            Returns complete showtime information including:
            - All showtime fields
            - Full movie details with genres and formats
            - Screen information with cinema name
            - Seat layout of the screen (ordered by row and seat number)
        """
    )
    @ApiResponses(
            value =
                    [
                            ApiResponse(
                                    responseCode = "200",
                                    description = "Showtime found",
                                    content =
                                            [
                                                    Content(
                                                            schema =
                                                                    Schema(
                                                                            implementation =
                                                                                    ShowtimeDetailDto::class
                                                                    )
                                                    )]
                            ),
                            ApiResponse(
                                    responseCode = "404",
                                    description = "Showtime not found",
                                    content =
                                            [
                                                    Content(
                                                            schema =
                                                                    Schema(
                                                                            implementation =
                                                                                    ApiError::class
                                                                    )
                                                    )]
                            )]
    )
    fun getShowtimeDetail(@PathVariable id: Long): ResponseEntity<ShowtimeDetailDto> {
        val detail = showtimeService.getShowtimeDetail(id)
        return if (detail != null) {
            ResponseEntity.ok(detail)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
