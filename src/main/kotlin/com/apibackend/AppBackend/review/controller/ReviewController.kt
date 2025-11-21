package com.apibackend.AppBackend.review.controller

import com.apibackend.AppBackend.review.dto.CreateReviewRequest
import com.apibackend.AppBackend.review.dto.ReviewResponse
import com.apibackend.AppBackend.review.dto.UpdateReviewRequest
import com.apibackend.AppBackend.review.service.ReviewService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ReviewController(private val reviewService: ReviewService) {

    /** Get reviews for a movie GET /api/movies/{movieId}/reviews */
    @GetMapping("/movies/{movieId}/reviews")
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

    /**
     * Create a new review POST /api/movies/{movieId}/reviews Requires authentication (userId from
     * session/token)
     */
    @PostMapping("/movies/{movieId}/reviews")
    fun createReview(
            @PathVariable movieId: Long,
            @Valid @RequestBody request: CreateReviewRequest,
            @RequestHeader("X-User-Id") userId: Long // Temporary: should come from JWT token
    ): ResponseEntity<ReviewResponse> {
        // Validate movieId matches request
        if (movieId != request.movieId) {
            return ResponseEntity.badRequest().build()
        }

        val review = reviewService.createReview(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(review)
    }

    /** Update a review PUT /api/reviews/{reviewId} Requires authentication */
    @PutMapping("/reviews/{reviewId}")
    fun updateReview(
            @PathVariable reviewId: Long,
            @Valid @RequestBody request: UpdateReviewRequest,
            @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<ReviewResponse> {
        val review = reviewService.updateReview(userId, reviewId, request)
        return ResponseEntity.ok(review)
    }

    /** Delete a review (soft delete) DELETE /api/reviews/{reviewId} Requires authentication */
    @DeleteMapping("/reviews/{reviewId}")
    fun deleteReview(
            @PathVariable reviewId: Long,
            @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<Map<String, String>> {
        reviewService.deleteReview(userId, reviewId)
        return ResponseEntity.ok(mapOf("message" to "Review deleted successfully"))
    }

    /**
     * Toggle helpful vote on a review POST /api/reviews/{reviewId}/helpful Requires authentication
     */
    @PostMapping("/reviews/{reviewId}/helpful")
    fun toggleHelpfulVote(
            @PathVariable reviewId: Long,
            @RequestHeader("X-User-Id") userId: Long
    ): ResponseEntity<Map<String, Any>> {
        val result = reviewService.toggleHelpfulVote(userId, reviewId)
        return ResponseEntity.ok(result)
    }
}
