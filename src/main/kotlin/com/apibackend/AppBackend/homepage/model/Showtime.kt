package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "showtimes")
data class Showtime(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "movie_id", nullable = false)
        val movie: Movie,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "screen_id", nullable = false)
        val screen: Screen,
        @Column(name = "start_time", nullable = false) val startTime: OffsetDateTime,
        @Column(name = "end_time", nullable = false) val endTime: OffsetDateTime,
        @Column(nullable = false) val status: String = "SCHEDULED",
        @Column(name = "available_seats") val availableSeats: Short? = null,
        @Column(name = "is_active") val active: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
