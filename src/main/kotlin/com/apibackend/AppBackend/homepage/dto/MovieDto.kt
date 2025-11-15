package com.apibackend.AppBackend.homepage.dto

import com.apibackend.AppBackend.homepage.model.MovieStatus
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class MovieDto(
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
        val genres: List<GenreDto>,
        val formats: List<FormatDto>
)

data class CreateMovieDto(
        @field:NotBlank(message = "title must not be blank") val title: String,
        val synopsis: String? = null,
        @field:Positive(message = "durationMin must be > 0") val durationMin: Short? = null,
        val releaseDate: LocalDate? = null,
        val status: MovieStatus = MovieStatus.COMING_SOON,
        val posterUrl: String? = null,
        val trailerUrl: String? = null,
        @field:DecimalMin(value = "0.0", message = "ratingAvg must be >= 0.0")
        @field:DecimalMax(value = "10.0", message = "ratingAvg must be <= 10.0")
        val ratingAvg: BigDecimal = BigDecimal.ZERO,
        @field:PositiveOrZero(message = "ratingCount must be >= 0") val ratingCount: Int = 0,
        val genreIds: List<Long> = emptyList(),
        val formatIds: List<Long> = emptyList()
)

data class UpdateMovieDto(
        @field:Size(min = 1, message = "title must not be empty when provided")
        val title: String? = null,
        val synopsis: String? = null,
        @field:Positive(message = "durationMin must be > 0") val durationMin: Short? = null,
        val releaseDate: LocalDate? = null,
        val status: MovieStatus? = null,
        val posterUrl: String? = null,
        val trailerUrl: String? = null,
        @field:DecimalMin(value = "0.0", message = "ratingAvg must be >= 0.0")
        @field:DecimalMax(value = "10.0", message = "ratingAvg must be <= 10.0")
        val ratingAvg: BigDecimal? = null,
        @field:PositiveOrZero(message = "ratingCount must be >= 0") val ratingCount: Int? = null,
        val genreIds: List<Long>? = null,
        val formatIds: List<Long>? = null
)

data class GenreDto(val id: Long, val name: String, val slug: String)

data class FormatDto(val id: Long, val code: String, val label: String)
