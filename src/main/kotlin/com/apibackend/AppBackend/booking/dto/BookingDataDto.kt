package com.apibackend.AppBackend.booking.dto

import com.apibackend.AppBackend.booking.model.SeatType
import java.math.BigDecimal

/**
 * Unified Booking Data Response
 * Contains all data needed for the booking ticket screen
 */
data class BookingDataResponse(
    val movie: MovieInfoDto,
    val cinema: CinemaInfoDto,
    val availableDates: List<AvailableDateDto>,
    val formats: List<ScreeningFormatDto>,
    val showtimes: Map<String, Map<String, List<ShowtimeSlotDto>>>
)

/**
 * Movie information for booking header
 */
data class MovieInfoDto(
    val id: Long,
    val title: String,
    val posterUrl: String?,
    val genres: List<String>,
    val duration: Int?,
    val durationFormatted: String?,
    val ratingAvg: BigDecimal
)

/**
 * Cinema information
 */
data class CinemaInfoDto(
    val id: Long,
    val name: String,
    val address: String,
    val city: String,
    val imageUrl: String?
)

/**
 * Available date for booking
 */
data class AvailableDateDto(
    val date: String,           // YYYY-MM-DD
    val dayOfWeek: String,      // Wednesday
    val dayOfWeekShort: String, // Wed
    val dayNumber: Int,         // 26
    val monthShort: String,     // Nov
    val isToday: Boolean
)

/**
 * Screening format (2D, 3D, IMAX...)
 */
data class ScreeningFormatDto(
    val id: Long,
    val name: String,
    val code: String,
    val isDefault: Boolean = false
)

/**
 * Showtime slot information
 */
data class ShowtimeSlotDto(
    val id: Long,
    val time: String,                   // HH:mm
    val startTime: String,              // ISO 8601
    val endTime: String,                // ISO 8601
    val screenId: Long,
    val screenName: String,
    val formatCode: String?,
    val availableSeats: Int,
    val totalSeats: Int,
    val prices: Map<SeatType, BigDecimal>,  // STANDARD -> 85000, VIP -> 110000
    val isAlmostFull: Boolean,
    val isSoldOut: Boolean
)

// ===== Seat Map DTOs =====

/**
 * Seat Map Response - called when user selects a showtime
 */
data class SeatMapResponse(
    val showtimeId: Long,
    val screenId: Long,
    val screenName: String,
    val rows: Int,
    val columns: Int,
    val seats: List<SeatInfoDto>,
    val prices: Map<SeatType, BigDecimal>
)

/**
 * Individual seat information
 */
data class SeatInfoDto(
    val id: String,             // Display code: "A1"
    val seatId: Long,           // Database ID
    val row: Int,               // 0-indexed
    val rowLabel: String,       // "A"
    val column: Int,            // 0-indexed
    val columnLabel: String,    // "1"
    val status: SeatStatus,
    val type: SeatType,
    val price: BigDecimal
)

enum class SeatStatus {
    AVAILABLE,
    SELECTED,
    BOOKED,
    LOCKED,
    BLOCKED
}

// ===== Booking Request/Response DTOs =====

/**
 * Create booking request
 */
data class CreateBookingRequest(
    val showtimeId: Long,
    val seatIds: List<Long>,
    val totalPrice: BigDecimal
)

/**
 * Booking response after creation
 */
data class BookingResponseDto(
    val id: Long,
    val bookingCode: String,
    val status: String,
    val movie: BookingMovieDto,
    val cinema: BookingCinemaDto,
    val showtime: BookingShowtimeDto,
    val seats: List<String>,
    val totalPrice: BigDecimal,
    val expiresAt: String,
    val createdAt: String
)

data class BookingMovieDto(
    val id: Long,
    val title: String
)

data class BookingCinemaDto(
    val id: Long,
    val name: String
)

data class BookingShowtimeDto(
    val id: Long,
    val date: String,
    val time: String,
    val screenName: String
)
