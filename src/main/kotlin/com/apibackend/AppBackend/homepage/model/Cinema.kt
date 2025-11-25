package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "cinemas")
data class Cinema(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false) val name: String,
        @Column(nullable = false) val address: String,
        @Column(nullable = false) val city: String,
        @Column val district: String? = null,
        @Column(name = "phone_number") val phoneNumber: String? = null,
        @Column val email: String? = null,
        @Column(precision = 10, scale = 8) val latitude: BigDecimal? = null,
        @Column(precision = 11, scale = 8) val longitude: BigDecimal? = null,
        @Column(name = "is_active") val active: Boolean = true,
        @Column(name = "created_at") val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at") val updatedAt: OffsetDateTime = OffsetDateTime.now()
)
