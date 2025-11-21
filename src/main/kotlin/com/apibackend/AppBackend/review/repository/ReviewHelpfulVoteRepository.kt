package com.apibackend.AppBackend.review.repository

import com.apibackend.AppBackend.review.model.ReviewHelpfulVote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReviewHelpfulVoteRepository : JpaRepository<ReviewHelpfulVote, Long> {

    @Query(
            """
        SELECT v FROM ReviewHelpfulVote v
        WHERE v.review.id = :reviewId
        AND v.user.id = :userId
    """
    )
    fun findByReviewIdAndUserId(
            @Param("reviewId") reviewId: Long,
            @Param("userId") userId: Long
    ): ReviewHelpfulVote?

    fun deleteByReviewIdAndUserId(reviewId: Long, userId: Long)
}
