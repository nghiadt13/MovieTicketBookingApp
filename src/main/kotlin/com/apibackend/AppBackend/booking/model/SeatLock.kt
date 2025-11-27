package com.apibackend.AppBackend.booking.model

import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.homepage.model.Showtime
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    name = "seat_locks",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_seat_locks_showtime_seat",
            columnNames = ["showtime_id", "seat_id"]
        )
    ]
)
data class SeatLock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    val showtime: Showtime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    val seat: Seat,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "locked_until", nullable = false)
    val lockedUntil: OffsetDateTime,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
