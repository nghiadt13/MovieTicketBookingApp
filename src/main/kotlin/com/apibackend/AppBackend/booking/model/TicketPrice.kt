package com.apibackend.AppBackend.booking.model

import com.apibackend.AppBackend.homepage.model.Showtime
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "ticket_prices",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_ticket_prices_showtime_seat_type",
            columnNames = ["showtime_id", "seat_type"]
        )
    ]
)
data class TicketPrice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    val showtime: Showtime,

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    val seatType: SeatType,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
