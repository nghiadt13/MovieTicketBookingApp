package com.apibackend.AppBackend.payment.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "combos")
data class Combo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(length = 500)
    val description: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "image_url", length = 500)
    val imageUrl: String? = null,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "display_order", nullable = false)
    val displayOrder: Int = 0,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
