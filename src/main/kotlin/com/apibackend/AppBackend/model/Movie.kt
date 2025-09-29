package com.apibackend.AppBackend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import com.apibackend.AppBackend.model.Format

@Entity
@Table(name = "movies")
data class Movie(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false) val title: String,
        @Column(columnDefinition = "TEXT") val synopsis: String? = null,
        @Column(name = "duration_min") val durationMin: Short? = null,
        @Column(name = "release_date") val releaseDate: LocalDate? = null,
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        val status: MovieStatus = MovieStatus.COMING_SOON,
        @Column(name = "poster_url", columnDefinition = "TEXT") val posterUrl: String? = null,
        @Column(name = "trailer_url", columnDefinition = "TEXT") val trailerUrl: String? = null,
        @Column(name = "rating_avg", precision = 3, scale = 1)
        val ratingAvg: BigDecimal = BigDecimal.ZERO,
        @Column(name = "rating_count") val ratingCount: Int = 0,
        @Column(name = "is_active") val isActive: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now(),
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "movie_genres",
                joinColumns = [JoinColumn(name = "movie_id")],
                inverseJoinColumns = [JoinColumn(name = "genre_id")]
        )
        val genres: Set<Genre> = emptySet(),
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "movie_formats",
                joinColumns = [JoinColumn(name = "movie_id")],
                inverseJoinColumns = [JoinColumn(name = "format_id")]
        )
        val formats: Set<Format> = emptySet() 

        
)
