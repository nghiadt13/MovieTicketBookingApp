package com.apibackend.AppBackend.review.model

import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.homepage.model.Movie
import jakarta.persistence.*
import java.time.OffsetDateTime
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType

@Entity
@Table(name = "movie_reviews")
data class MovieReview(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "movie_id", nullable = false)
        val movie: Movie,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
        @Column(name = "booking_id", nullable = false) val bookingId: Long,
        @Column(nullable = false) val rating: Short,
        @Column(name = "comment_text", columnDefinition = "TEXT") val commentText: String? = null,
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        @JdbcType(PostgreSQLEnumJdbcType::class)
        val status: ReviewStatus = ReviewStatus.APPROVED,
        @Column(name = "is_spoiler", nullable = false) val isSpoiler: Boolean = false,
        @Column(name = "helpful_count", nullable = false) val helpfulCount: Int = 0,
        @Column(name = "is_edited", nullable = false) val isEdited: Boolean = false,
        @Column(name = "deleted_at") val deletedAt: OffsetDateTime? = null,
        @Column(name = "created_at", nullable = false)
        val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "updated_at", nullable = false)
        val updatedAt: OffsetDateTime = OffsetDateTime.now(),
        @OneToMany(mappedBy = "review", cascade = [CascadeType.ALL], orphanRemoval = true)
        val images: MutableList<ReviewImage> = mutableListOf()
)
