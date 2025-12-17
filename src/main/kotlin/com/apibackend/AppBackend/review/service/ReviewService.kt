package com.apibackend.AppBackend.review.service

import com.apibackend.AppBackend.auth.repository.UserRepository
import com.apibackend.AppBackend.homepage.repository.MovieRepository
import com.apibackend.AppBackend.review.dto.*
import com.apibackend.AppBackend.review.exception.*
import com.apibackend.AppBackend.review.model.*
import com.apibackend.AppBackend.review.repository.MovieReviewRepository
import com.apibackend.AppBackend.review.repository.ReviewHelpfulVoteRepository
import java.time.OffsetDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
        private val reviewRepository: MovieReviewRepository,
        private val voteRepository: ReviewHelpfulVoteRepository,
        private val movieRepository: MovieRepository,
        private val userRepository: UserRepository,
        private val bookingValidationService: BookingValidationService
) {

        @Transactional
        fun createReview(userId: Long, request: CreateReviewRequest): ReviewResponse {
                // Validate user exists
                val user =
                        userRepository.findById(userId).orElseThrow {
                                IllegalArgumentException("User not found")
                        }

                // Validate movie exists
                val movie =
                        movieRepository.findById(request.movieId).orElseThrow {
                                IllegalArgumentException("Movie not found")
                        }

                // Check if user already reviewed this movie
                val existingReview =
                        reviewRepository.findByUserIdAndMovieId(userId, request.movieId)
                if (existingReview != null) {
                        throw ReviewAlreadyExistsException("You have already reviewed this movie")
                }

                // Validate booking (chỉ khi có bookingId)
                if (request.bookingId != null) {
                        bookingValidationService.validateBookingForReview(
                                userId,
                                request.bookingId,
                                request.movieId
                        )
                }

                // Create review
                val review =
                        MovieReview(
                                movie = movie,
                                user = user,
                                bookingId = request.bookingId,
                                rating = request.rating,
                                commentText = request.commentText,
                                isSpoiler = request.isSpoiler,
                                status = ReviewStatus.APPROVED
                        )

                // Add images
                request.imageUrls.forEachIndexed { index, url ->
                        review.images.add(
                                ReviewImage(
                                        review = review,
                                        imageUrl = url,
                                        displayOrder = index.toShort()
                                )
                        )
                }

                val savedReview = reviewRepository.save(review)

                // Update movie rating
                updateMovieRating(movie.id)

                return mapToResponse(savedReview)
        }

        @Transactional
        fun updateReview(
                userId: Long,
                reviewId: Long,
                request: UpdateReviewRequest
        ): ReviewResponse {
                val review =
                        reviewRepository.findByIdAndNotDeleted(reviewId)
                                ?: throw ReviewNotFoundException("Review not found")

                // Check ownership
                if (review.user.id != userId) {
                        throw UnauthorizedReviewAccessException(
                                "You can only update your own reviews"
                        )
                }

                // Create updated review
                val updatedReview =
                        review.copy(
                                rating = request.rating,
                                commentText = request.commentText,
                                isSpoiler = request.isSpoiler,
                                isEdited = true,
                                updatedAt = OffsetDateTime.now()
                        )

                // Clear and update images
                updatedReview.images.clear()
                request.imageUrls.forEachIndexed { index, url ->
                        updatedReview.images.add(
                                ReviewImage(
                                        review = updatedReview,
                                        imageUrl = url,
                                        displayOrder = index.toShort()
                                )
                        )
                }

                val savedReview = reviewRepository.save(updatedReview)

                // Update movie rating if rating changed
                if (review.rating != request.rating) {
                        updateMovieRating(review.movie.id)
                }

                return mapToResponse(savedReview)
        }

        @Transactional
        fun deleteReview(userId: Long, reviewId: Long) {
                val review =
                        reviewRepository.findByIdAndNotDeleted(reviewId)
                                ?: throw ReviewNotFoundException("Review not found")

                // Check ownership
                if (review.user.id != userId) {
                        throw UnauthorizedReviewAccessException(
                                "You can only delete your own reviews"
                        )
                }

                // Soft delete
                val deletedReview = review.copy(deletedAt = OffsetDateTime.now())
                reviewRepository.save(deletedReview)

                // Update movie rating
                updateMovieRating(review.movie.id)
        }

        @Transactional(readOnly = true)
        fun getMovieReviews(
                movieId: Long,
                sortBy: String = "recent",
                pageable: Pageable
        ): Page<ReviewResponse> {
                val reviews =
                        when (sortBy) {
                                "helpful" ->
                                        reviewRepository.findByMovieIdAndStatusOrderByHelpful(
                                                movieId,
                                                ReviewStatus.APPROVED,
                                                pageable
                                        )
                                else ->
                                        reviewRepository.findByMovieIdAndStatus(
                                                movieId,
                                                ReviewStatus.APPROVED,
                                                pageable
                                        )
                        }

                return reviews.map { mapToResponse(it) }
        }

        @Transactional
        fun toggleHelpfulVote(userId: Long, reviewId: Long): Map<String, Any> {
                val review =
                        reviewRepository.findByIdAndNotDeleted(reviewId)
                                ?: throw ReviewNotFoundException("Review not found")

                val user =
                        userRepository.findById(userId).orElseThrow {
                                IllegalArgumentException("User not found")
                        }

                val existingVote = voteRepository.findByReviewIdAndUserId(reviewId, userId)

                return if (existingVote != null) {
                        // Remove vote
                        voteRepository.delete(existingVote)
                        val updatedReview =
                                review.copy(helpfulCount = maxOf(0, review.helpfulCount - 1))
                        reviewRepository.save(updatedReview)
                        mapOf("voted" to false, "helpfulCount" to updatedReview.helpfulCount)
                } else {
                        // Add vote
                        val vote = ReviewHelpfulVote(review = review, user = user)
                        voteRepository.save(vote)
                        val updatedReview = review.copy(helpfulCount = review.helpfulCount + 1)
                        reviewRepository.save(updatedReview)
                        mapOf("voted" to true, "helpfulCount" to updatedReview.helpfulCount)
                }
        }

        /**
         * Cập nhật rating trung bình và số lượng reviews của phim
         */
        private fun updateMovieRating(movieId: Long) {
                val movie = movieRepository.findById(movieId).orElse(null) ?: return
                
                // Tính rating trung bình và số lượng reviews
                val avgRating = reviewRepository.calculateAverageRating(movieId) ?: 0.0
                val reviewCount = reviewRepository.countByMovieId(movieId)
                
                // Update movie entity
                val updatedMovie = movie.copy(
                        ratingAvg = java.math.BigDecimal.valueOf(avgRating).setScale(1, java.math.RoundingMode.HALF_UP),
                        ratingCount = reviewCount
                )
                movieRepository.save(updatedMovie)
        }

        private fun mapToResponse(review: MovieReview): ReviewResponse {
                return ReviewResponse(
                        id = review.id,
                        movieId = review.movie.id,
                        user =
                                ReviewUserDto(
                                        id = review.user.id
                                                        ?: throw IllegalStateException(
                                                                "User ID cannot be null"
                                                        ),
                                        displayName = review.user.displayName,
                                        avatarUrl = review.user.avatarUrl
                                ),
                        rating = review.rating,
                        commentText = review.commentText,
                        isSpoiler = review.isSpoiler,
                        isEdited = review.isEdited,
                        helpfulCount = review.helpfulCount,
                        images =
                                review.images.sortedBy { it.displayOrder }.map {
                                        ReviewImageDto(
                                                id = it.id,
                                                imageUrl = it.imageUrl,
                                                displayOrder = it.displayOrder
                                        )
                                },
                        createdAt = review.createdAt,
                        updatedAt = review.updatedAt
                )
        }
}
