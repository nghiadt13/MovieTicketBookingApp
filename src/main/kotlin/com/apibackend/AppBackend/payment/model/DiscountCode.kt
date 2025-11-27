package com.apibackend.AppBackend.payment.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "discount_codes")
data class DiscountCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(length = 255)
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val discountType: DiscountType,

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    val discountValue: BigDecimal,

    @Column(name = "min_order_amount", precision = 10, scale = 2)
    val minOrderAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    val maxDiscountAmount: BigDecimal? = null,

    @Column(name = "usage_limit")
    val usageLimit: Int? = null,

    @Column(name = "used_count", nullable = false)
    val usedCount: Int = 0,

    @Column(name = "valid_from", nullable = false)
    val validFrom: OffsetDateTime,

    @Column(name = "valid_until", nullable = false)
    val validUntil: OffsetDateTime,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
