package com.apibackend.AppBackend.review.controller

import com.apibackend.AppBackend.review.dto.CreateReviewRequest
import com.apibackend.AppBackend.review.dto.ReviewResponse
import com.apibackend.AppBackend.review.dto.UpdateReviewRequest
import com.apibackend.AppBackend.review.service.ReviewService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Tag(name = "Reviews", description = "Movie review endpoints")
class ReviewController(private val reviewService: ReviewService) {

    /**
     * Lấy userId từ JWT token trong SecurityContext
     */
    private fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as Long
    }

    @GetMapping("/movies/{movieId}/reviews")
    @Operation(summary = "Get reviews for a movie")
    fun getMovieReviews(
            @PathVariable movieId: Long,
            @RequestParam(defaultValue = "recent") sortBy: String,
            @RequestParam(defaultValue = "0") page: Int,
            @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<ReviewResponse>> {
        val pageable = PageRequest.of(page, size)
        val reviews = reviewService.getMovieReviews(movieId, sortBy, pageable)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping("/movies/{movieId}/reviews")
    @Operation(summary = "Create a new review", description = "Requires authentication via JWT token")
    fun createReview(
            @PathVariable movieId: Long,
            @Valid @RequestBody request: CreateReviewRequest
    ): ResponseEntity<ReviewResponse> {
        // Validate movieId matches request
        if (movieId != request.movieId) {
            return ResponseEntity.badRequest().build()
        }

        val userId = getCurrentUserId()
        val review = reviewService.createReview(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "Update a review", description = "Only owner can update their review")
    fun updateReview(
            @PathVariable reviewId: Long,
            @Valid @RequestBody request: UpdateReviewRequest
    ): ResponseEntity<ReviewResponse> {
        val userId = getCurrentUserId()
        val review = reviewService.updateReview(userId, reviewId, request)
        return ResponseEntity.ok(review)
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review (soft delete)", description = "Only owner can delete their review")
    fun deleteReview(@PathVariable reviewId: Long): ResponseEntity<Map<String, String>> {
        val userId = getCurrentUserId()
        reviewService.deleteReview(userId, reviewId)
        return ResponseEntity.ok(mapOf("message" to "Review deleted successfully"))
    }

    @PostMapping("/reviews/{reviewId}/helpful")
    @Operation(summary = "Toggle helpful vote on a review")
    fun toggleHelpfulVote(@PathVariable reviewId: Long): ResponseEntity<Map<String, Any>> {
        val userId = getCurrentUserId()
        val result = reviewService.toggleHelpfulVote(userId, reviewId)
        return ResponseEntity.ok(result)
    }
}
