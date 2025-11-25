package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "screens")
data class Screen(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "cinema_id", nullable = false)
        val cinema: Cinema,
        @Column(nullable = false) val name: String,
        @Column(name = "total_seats", nullable = false) val totalSeats: Short,
        @Column(name = "screen_type") val screenType: String? = null,
        @Column(name = "is_active") val active: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
