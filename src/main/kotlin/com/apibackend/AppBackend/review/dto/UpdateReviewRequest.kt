package com.apibackend.AppBackend.review.dto

import jakarta.validation.constraints.*

data class UpdateReviewRequest(
        @field:NotNull(message = "Rating is required")
        @field:Min(value = 1, message = "Rating must be between 1 and 10")
        @field:Max(value = 10, message = "Rating must be between 1 and 10")
        val rating: Short,
        @field:Size(max = 5000, message = "Comment text must not exceed 5000 characters")
        val commentText: String? = null,
        val isSpoiler: Boolean = false,
        @field:Size(max = 5, message = "Maximum 5 images allowed")
        val imageUrls: List<@NotBlank(message = "Image URL cannot be blank") String> = emptyList()
)
