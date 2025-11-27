package com.apibackend.AppBackend.payment.model

import com.apibackend.AppBackend.booking.model.Booking
import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    val transactionId: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    val booking: Booking,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    val paymentMethod: PaymentMethod,

    @Column(name = "ticket_price", nullable = false, precision = 10, scale = 2)
    val ticketPrice: BigDecimal,

    @Column(name = "ticket_count", nullable = false)
    val ticketCount: Int,

    @Column(name = "combo_price", nullable = false, precision = 10, scale = 2)
    val comboPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    val discountAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    val totalAmount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_code_id")
    val discountCode: DiscountCode? = null,

    @Column(name = "qr_code_url", length = 500)
    val qrCodeUrl: String? = null,

    @Column(name = "gateway_transaction_id", length = 100)
    val gatewayTransactionId: String? = null,

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    val gatewayResponse: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val status: PaymentStatus = PaymentStatus.PENDING,

    @Column(name = "paid_at")
    val paidAt: OffsetDateTime? = null,

    @Column(name = "failed_at")
    val failedAt: OffsetDateTime? = null,

    @Column(name = "failure_reason", length = 500)
    val failureReason: String? = null,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),

    @OneToMany(mappedBy = "payment", cascade = [CascadeType.ALL], orphanRemoval = true)
    val combos: MutableList<PaymentCombo> = mutableListOf()
)
