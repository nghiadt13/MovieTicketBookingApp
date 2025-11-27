package com.apibackend.AppBackend.booking.model

import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.homepage.model.Showtime
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType

@Entity
@Table(name = "bookings")
data class Booking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "booking_code", nullable = false, unique = true)
    val bookingCode: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    val showtime: Showtime,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val status: BookingStatus = BookingStatus.PENDING,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @Column(name = "payment_method")
    val paymentMethod: String? = null,

    @Column(name = "payment_transaction_id")
    val paymentTransactionId: String? = null,

    @Column(name = "paid_at")
    val paidAt: OffsetDateTime? = null,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: OffsetDateTime,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(mappedBy = "booking", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<BookingItem> = mutableListOf()
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}
