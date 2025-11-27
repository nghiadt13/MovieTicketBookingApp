package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.booking.model.Booking
import com.apibackend.AppBackend.booking.model.BookingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BookingRepository : JpaRepository<Booking, Long> {

    fun findByBookingCode(bookingCode: String): Booking?

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Booking>

    fun findByUserIdAndStatus(userId: Long, status: BookingStatus): List<Booking>

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.showtime s
        JOIN FETCH s.movie
        JOIN FETCH s.screen sc
        JOIN FETCH sc.cinema
        WHERE b.id = :id
    """)
    fun findByIdWithDetails(@Param("id") id: Long): Booking?

    @Query("""
        SELECT b FROM Booking b
        WHERE b.showtime.id = :showtimeId
        AND b.status IN ('PENDING', 'CONFIRMED')
    """)
    fun findActiveBookingsByShowtimeId(@Param("showtimeId") showtimeId: Long): List<Booking>

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.showtime.id = :showtimeId
        AND b.status = 'PENDING'
        AND b.expiresAt < CURRENT_TIMESTAMP
    """)
    fun countExpiredPendingBookings(@Param("showtimeId") showtimeId: Long): Long
}
