package com.apibackend.AppBackend.payment.model

import com.apibackend.AppBackend.homepage.model.Cinema
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "cinema_combos")
data class CinemaCombo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    val cinema: Cinema,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "combo_id", nullable = false)
    val combo: Combo,

    @Column(name = "is_available", nullable = false)
    val isAvailable: Boolean = true,

    @Column(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()
)
