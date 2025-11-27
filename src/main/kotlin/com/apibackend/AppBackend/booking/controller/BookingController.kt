package com.apibackend.AppBackend.booking.controller

import com.apibackend.AppBackend.booking.dto.BookingResponseDto
import com.apibackend.AppBackend.booking.dto.CreateBookingRequest
import com.apibackend.AppBackend.booking.service.BookingService
import com.apibackend.AppBackend.common.config.ApiError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Booking management endpoints")
class BookingController(
    private val bookingService: BookingService
) {

    @PostMapping
    @Operation(
        summary = "Create a new booking",
        description = """
            Creates a new booking for the specified showtime and seats.

            The booking will be in PENDING status and must be paid within 15 minutes.
            After payment, status will change to CONFIRMED.
            If not paid within the time limit, status changes to EXPIRED.

            Note: userId should come from the authenticated user's session/token.
            For testing, you can pass it as a request header.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Booking created successfully",
                content = [Content(schema = Schema(implementation = BookingResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request (e.g., invalid seats, past showtime)",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Showtime or seats not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Seats already booked or locked",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun createBooking(
        @Parameter(description = "User ID (from auth token)", required = true)
        @RequestHeader("X-User-Id") userId: Long,
        @Valid @RequestBody request: CreateBookingRequest
    ): ResponseEntity<BookingResponseDto> {
        val booking = bookingService.createBooking(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(booking)
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get booking by ID",
        description = "Returns booking details including movie, cinema, showtime, and seats"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Booking found",
                content = [Content(schema = Schema(implementation = BookingResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun getBookingById(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<BookingResponseDto> {
        val booking = bookingService.getBookingById(id)
        return ResponseEntity.ok(booking)
    }

    @GetMapping("/code/{bookingCode}")
    @Operation(
        summary = "Get booking by code",
        description = "Returns booking details by booking code (e.g., BK20251126ABC123)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Booking found",
                content = [Content(schema = Schema(implementation = BookingResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun getBookingByCode(
        @Parameter(description = "Booking code", required = true, example = "BK20251126ABC123")
        @PathVariable bookingCode: String
    ): ResponseEntity<BookingResponseDto> {
        val booking = bookingService.getBookingByCode(bookingCode)
        return ResponseEntity.ok(booking)
    }

    @GetMapping("/user")
    @Operation(
        summary = "Get user's bookings",
        description = "Returns all bookings for the authenticated user, ordered by creation date (newest first)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Bookings retrieved successfully",
                content = [Content(schema = Schema(implementation = Array<BookingResponseDto>::class))]
            )
        ]
    )
    fun getUserBookings(
        @Parameter(description = "User ID (from auth token)", required = true)
        @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<List<BookingResponseDto>> {
        val bookings = bookingService.getUserBookings(userId)
        return ResponseEntity.ok(bookings)
    }

    @PostMapping("/{id}/cancel")
    @Operation(
        summary = "Cancel a booking",
        description = "Cancels a pending booking. Only the booking owner can cancel."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Booking cancelled successfully",
                content = [Content(schema = Schema(implementation = BookingResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Booking cannot be cancelled (e.g., already confirmed)",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun cancelBooking(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable id: Long,
        @Parameter(description = "User ID (from auth token)", required = true)
        @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<BookingResponseDto> {
        val booking = bookingService.cancelBooking(id, userId)
        return ResponseEntity.ok(booking)
    }

    // ===== Seat Lock Endpoints =====

    @PostMapping("/seats/lock")
    @Operation(
        summary = "Lock seats temporarily",
        description = """
            Locks selected seats for 10 minutes while the user completes the booking.
            Other users will see these seats as unavailable.

            Call this when user selects seats but hasn't completed payment yet.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Seats locked successfully"
            ),
            ApiResponse(
                responseCode = "409",
                description = "Some seats are already booked or locked"
            )
        ]
    )
    fun lockSeats(
        @Parameter(description = "User ID (from auth token)", required = true)
        @RequestHeader("X-User-Id") userId: Long,
        @RequestParam showtimeId: Long,
        @RequestParam seatIds: List<Long>
    ): ResponseEntity<Map<String, Any>> {
        val success = bookingService.lockSeats(userId, showtimeId, seatIds)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true, "message" to "Seats locked for 10 minutes"))
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body(mapOf("success" to false, "message" to "Some seats are no longer available"))
        }
    }

    @DeleteMapping("/seats/lock")
    @Operation(
        summary = "Unlock seats",
        description = "Releases seat locks for a showtime. Call this when user leaves the booking flow."
    )
    fun unlockSeats(
        @Parameter(description = "User ID (from auth token)", required = true)
        @RequestHeader("X-User-Id") userId: Long,
        @RequestParam showtimeId: Long
    ): ResponseEntity<Map<String, Any>> {
        bookingService.unlockSeats(userId, showtimeId)
        return ResponseEntity.ok(mapOf("success" to true, "message" to "Seats unlocked"))
    }
}
