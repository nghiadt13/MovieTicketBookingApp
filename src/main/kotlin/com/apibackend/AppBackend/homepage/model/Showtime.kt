package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType

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
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "format_id")
        val format: Format? = null,
        @Column(name = "start_time", nullable = false) val startTime: OffsetDateTime,
        @Column(name = "end_time", nullable = false) val endTime: OffsetDateTime,
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @JdbcType(PostgreSQLEnumJdbcType::class)
        val status: ShowtimeStatus = ShowtimeStatus.SCHEDULED,
        @Column(name = "available_seats") val availableSeats: Short? = null,
        @Column(name = "is_active") val active: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

enum class ShowtimeStatus {
    SCHEDULED,
    SELLING,
    FULL,
    COMPLETED,
    CANCELLED
}
