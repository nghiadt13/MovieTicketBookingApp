package com.apibackend.AppBackend.review.model

import com.apibackend.AppBackend.auth.model.User
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "review_helpful_votes")
data class ReviewHelpfulVote(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "review_id", nullable = false)
        val review: MovieReview,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
        @Column(name = "created_at", nullable = false)
        val createdAt: OffsetDateTime = OffsetDateTime.now()
)
