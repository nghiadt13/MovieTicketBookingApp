package com.apibackend.AppBackend.payment.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "payment_combos")
data class PaymentCombo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    val payment: Payment,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_id", nullable = false)
    val combo: Combo,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val subtotal: BigDecimal,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
