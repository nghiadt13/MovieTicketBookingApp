package com.apibackend.AppBackend.booking.controller

import com.apibackend.AppBackend.booking.dto.BookingDataResponse
import com.apibackend.AppBackend.booking.dto.SeatMapResponse
import com.apibackend.AppBackend.booking.service.BookingDataService
import com.apibackend.AppBackend.common.config.ApiError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/booking")
@Tag(name = "Booking Data", description = "Unified booking data endpoints for ticket booking flow")
class BookingDataController(
    private val bookingDataService: BookingDataService
) {

    @GetMapping("/data")
    @Operation(
        summary = "Get unified booking data",
        description = """
            Returns all data needed for the booking ticket screen in a single request:
            - Movie information (title, genres, duration, poster)
            - Cinema information (name, address)
            - Available dates with showtimes
            - Available screening formats (2D, 3D, IMAX...)
            - Showtimes grouped by date and format
            - Ticket prices for each showtime

            Frontend should call this API once when entering the booking screen,
            then call the seat map API when user selects a specific showtime.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Booking data retrieved successfully",
                content = [Content(schema = Schema(implementation = BookingDataResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Movie or Cinema not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun getBookingData(
        @Parameter(description = "Movie ID", required = true, example = "1")
        @RequestParam movieId: Long,
        @Parameter(description = "Cinema ID", required = true, example = "1")
        @RequestParam cinemaId: Long
    ): ResponseEntity<BookingDataResponse> {
        val response = bookingDataService.getBookingData(movieId, cinemaId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/showtimes/{showtimeId}/seats")
    @Operation(
        summary = "Get seat map for a showtime",
        description = """
            Returns the seat layout and availability for a specific showtime:
            - Screen information
            - Grid dimensions (rows x columns)
            - All seats with their status (AVAILABLE, BOOKED, LOCKED)
            - Seat types and prices

            Call this API when user selects a showtime from the booking data.
            Seat status:
            - AVAILABLE: Can be selected
            - BOOKED: Already purchased (shown as X)
            - LOCKED: Temporarily held by another user
            - BLOCKED: Unavailable (maintenance)
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Seat map retrieved successfully",
                content = [Content(schema = Schema(implementation = SeatMapResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Showtime not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun getSeatMap(
        @Parameter(description = "Showtime ID", required = true, example = "101")
        @PathVariable showtimeId: Long
    ): ResponseEntity<SeatMapResponse> {
        val response = bookingDataService.getSeatMap(showtimeId)
        return ResponseEntity.ok(response)
    }
}
