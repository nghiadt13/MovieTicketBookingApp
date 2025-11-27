package com.apibackend.AppBackend.payment.model

import jakarta.persistence.*
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import java.time.OffsetDateTime

@Entity
@Table(name = "payment_methods")
data class PaymentMethod(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(length = 255)
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "icon_type", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val iconType: PaymentIconType,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int = 0,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
