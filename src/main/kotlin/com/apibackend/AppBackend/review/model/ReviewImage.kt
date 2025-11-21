package com.apibackend.AppBackend.review.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "review_images")
data class ReviewImage(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "review_id", nullable = false)
        val review: MovieReview,
        @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
        val imageUrl: String,
        @Column(name = "display_order", nullable = false) val displayOrder: Short = 0,
        @Column(name = "created_at", nullable = false)
        val createdAt: OffsetDateTime = OffsetDateTime.now()
)
