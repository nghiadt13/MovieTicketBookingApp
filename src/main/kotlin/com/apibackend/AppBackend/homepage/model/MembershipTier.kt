package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "membership_tiers")
data class MembershipTier(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false, unique = true, length = 50) val name: String,
        @Column(name = "rank_order", nullable = false, unique = true) val rankOrder: Int,
        @Column(name = "spending_required", nullable = false, precision = 10, scale = 2)
        val spendingRequired: BigDecimal,
        @Column(name = "discount_percent", precision = 5, scale = 2)
        val discountPercent: BigDecimal? = BigDecimal.ZERO,
        @Column(name = "points_multiplier", precision = 3, scale = 2)
        val pointsMultiplier: BigDecimal? = BigDecimal.ONE,
        @Column(name = "image_url", length = 500) val imageUrl: String? = null,
        @Column(columnDefinition = "TEXT") val description: String? = null
)
