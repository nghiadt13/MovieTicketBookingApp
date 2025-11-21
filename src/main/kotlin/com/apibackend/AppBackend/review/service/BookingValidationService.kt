package com.apibackend.AppBackend.review.service

import com.apibackend.AppBackend.review.exception.InvalidBookingException
import com.apibackend.AppBackend.review.exception.ShowtimeNotCompletedException
import java.time.OffsetDateTime
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookingValidationService(private val jdbcTemplate: JdbcTemplate) {

    /**
     * Validate that user can review based on booking
     * - Booking must exist
     * - Booking must belong to user
     * - Booking status must be CONFIRMED
     * - Showtime must have ended
     */
    fun validateBookingForReview(userId: Long, bookingId: Long, movieId: Long) {
        val sql =
                """
            SELECT 
                b.user_id,
                b.status,
                s.end_time,
                s.movie_id
            FROM bookings b
            JOIN showtimes s ON b.showtime_id = s.id
            WHERE b.id = ?
        """

        val result =
                jdbcTemplate.query(
                        sql,
                        { rs, _ ->
                            mapOf(
                                    "userId" to rs.getLong("user_id"),
                                    "status" to rs.getString("status"),
                                    "endTime" to
                                            rs.getObject("end_time", OffsetDateTime::class.java),
                                    "movieId" to rs.getLong("movie_id")
                            )
                        },
                        bookingId
                )

        if (result.isEmpty()) {
            throw InvalidBookingException("Booking not found")
        }

        val booking = result[0]

        // Check ownership
        if (booking["userId"] != userId) {
            throw InvalidBookingException("This booking does not belong to you")
        }

        // Check movie matches
        if (booking["movieId"] != movieId) {
            throw InvalidBookingException("This booking is not for the specified movie")
        }

        // Check status
        if (booking["status"] != "CONFIRMED") {
            throw InvalidBookingException("Only confirmed bookings can be reviewed")
        }

        // Check showtime has ended
        val endTime = booking["endTime"] as OffsetDateTime
        if (endTime.isAfter(OffsetDateTime.now())) {
            throw ShowtimeNotCompletedException("You can only review after the movie has ended")
        }
    }
}
