package com.apibackend.AppBackend.booking.dto

import com.apibackend.AppBackend.booking.model.SeatType
import com.apibackend.AppBackend.homepage.model.MovieStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime

// Main response DTO
data class ShowtimeDetailDto(
        val showtime: ShowtimeDto,
        val movie: MovieDetailDto,
        val screen: ScreenDetailDto,
        val seats: List<SeatDto>
)

// Showtime DTO - all fields
data class ShowtimeDto(
        val id: Long,
        val movieId: Long,
        val screenId: Long,
        val startTime: OffsetDateTime,
        val endTime: OffsetDateTime,
        val status: String,
        val availableSeats: Short?,
        val isActive: Boolean,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime
)

// Movie DTO - all fields
data class MovieDetailDto(
        val id: Long,
        val title: String,
        val synopsis: String?,
        val durationMin: Short?,
        val releaseDate: LocalDate?,
        val status: MovieStatus,
        val posterUrl: String?,
        val trailerUrl: String?,
        val ratingAvg: BigDecimal,
        val ratingCount: Int,
        val isActive: Boolean,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime,
        val genres: List<GenreDto>,
        val formats: List<FormatDto>
)

data class GenreDto(val id: Long, val name: String, val slug: String)

data class FormatDto(val id: Long, val code: String, val label: String)

// Screen DTO - all fields
data class ScreenDetailDto(
        val id: Long,
        val cinemaId: Long,
        val cinemaName: String,
        val name: String,
        val totalSeats: Short,
        val screenType: String?,
        val isActive: Boolean,
        val createdAt: OffsetDateTime,
        val updatedAt: OffsetDateTime
)

// Seat DTO
data class SeatDto(
        val id: Long,
        val screenId: Long,
        val rowName: String,
        val seatNumber: Short,
        val seatType: SeatType,
        val isActive: Boolean,
        val createdAt: OffsetDateTime
)
