package com.apibackend.AppBackend.review.dto

import java.time.OffsetDateTime

data class ReviewResponse(
    val id: Long,
    val movieId: Long,
    val user: ReviewUserDto,
    val rating: Short,
    val commentText: String?,
    val isSpoiler: Boolean,
    val isEdited: Boolean,
    val helpfulCount: Int,
    val images: List<ReviewImageDto>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime
)

data class ReviewUserDto(
    val id: Long,
    val displayName: String,
    val avatarUrl: String?
)

data class ReviewImageDto(
    val id: Long,
    val imageUrl: String,
    val displayOrder: Short
)
