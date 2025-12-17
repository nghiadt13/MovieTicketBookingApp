package com.apibackend.AppBackend.review.repository

import com.apibackend.AppBackend.review.model.MovieReview
import com.apibackend.AppBackend.review.model.ReviewStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MovieReviewRepository : JpaRepository<MovieReview, Long> {

    @Query(
            """
        SELECT r FROM MovieReview r
        WHERE r.movie.id = :movieId
        AND r.status = :status
        AND r.deletedAt IS NULL
        ORDER BY r.createdAt DESC
    """
    )
    fun findByMovieIdAndStatus(
            @Param("movieId") movieId: Long,
            @Param("status") status: ReviewStatus,
            pageable: Pageable
    ): Page<MovieReview>

    @Query(
            """
        SELECT r FROM MovieReview r
        WHERE r.movie.id = :movieId
        AND r.status = :status
        AND r.deletedAt IS NULL
        ORDER BY r.helpfulCount DESC, r.createdAt DESC
    """
    )
    fun findByMovieIdAndStatusOrderByHelpful(
            @Param("movieId") movieId: Long,
            @Param("status") status: ReviewStatus,
            pageable: Pageable
    ): Page<MovieReview>

    @Query(
            """
        SELECT r FROM MovieReview r
        WHERE r.user.id = :userId
        AND r.movie.id = :movieId
        AND r.deletedAt IS NULL
    """
    )
    fun findByUserIdAndMovieId(
            @Param("userId") userId: Long,
            @Param("movieId") movieId: Long
    ): MovieReview?

    @Query(
            """
        SELECT r FROM MovieReview r
        WHERE r.id = :reviewId
        AND r.deletedAt IS NULL
    """
    )
    fun findByIdAndNotDeleted(@Param("reviewId") reviewId: Long): MovieReview?

    /**
     * Tính trung bình rating của phim (chỉ tính reviews APPROVED và chưa xóa)
     */
    @Query(
            """
        SELECT AVG(r.rating) FROM MovieReview r
        WHERE r.movie.id = :movieId
        AND r.status = 'APPROVED'
        AND r.deletedAt IS NULL
    """
    )
    fun calculateAverageRating(@Param("movieId") movieId: Long): Double?

    /**
     * Đếm số lượng reviews của phim
     */
    @Query(
            """
        SELECT COUNT(r) FROM MovieReview r
        WHERE r.movie.id = :movieId
        AND r.status = 'APPROVED'
        AND r.deletedAt IS NULL
    """
    )
    fun countByMovieId(@Param("movieId") movieId: Long): Int
}
