package com.apibackend.AppBackend.movies.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "carousel_items")
class CarouselItem(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(nullable = false) val title: String,
        @Column(name = "image_url", nullable = false) val imageUrl: String,
        @Column(columnDefinition = "TEXT") val content: String? = null,
        @Column(name = "target_url") val targetUrl: String? = null,
        @Column(name = "is_active", nullable = false) val isActive: Boolean = true,
        @Column(name = "created_at", nullable = false, updatable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),
        @Column(name = "updated_at", nullable = false)
        var updatedAt: LocalDateTime = LocalDateTime.now()
)
