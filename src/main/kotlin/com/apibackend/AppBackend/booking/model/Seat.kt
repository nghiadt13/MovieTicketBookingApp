package com.apibackend.AppBackend.booking.model

import com.apibackend.AppBackend.homepage.model.Screen
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "seats")
data class Seat(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "screen_id", nullable = false)
        val screen: Screen,
        @Column(name = "row_name", nullable = false) val rowName: String,
        @Column(name = "seat_number", nullable = false) val seatNumber: Short,
        @Enumerated(EnumType.STRING)
        @Column(name = "seat_type", nullable = false)
        val seatType: SeatType = SeatType.STANDARD,
        @Column(name = "is_active") val active: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now()
)

enum class SeatType {
    STANDARD,
    VIP,
    COUPLE,
    DELUXE
}
