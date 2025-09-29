package com.apibackend.AppBackend.dto

import com.apibackend.AppBackend.model.MovieStatus
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

data class GenreDto(val id: Long, val name: String, val slug: String)

data class FormatDto(val id: Long, val code: String, val label: String)
