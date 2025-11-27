package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.booking.model.BookingItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BookingItemRepository : JpaRepository<BookingItem, Long> {

    fun findByBookingId(bookingId: Long): List<BookingItem>

    @Query("""
        SELECT bi FROM BookingItem bi
        JOIN bi.booking b
        WHERE b.showtime.id = :showtimeId
        AND b.status IN ('PENDING', 'CONFIRMED')
    """)
    fun findBookedSeatsByShowtimeId(@Param("showtimeId") showtimeId: Long): List<BookingItem>

    @Query("""
        SELECT bi.seat.id FROM BookingItem bi
        JOIN bi.booking b
        WHERE b.showtime.id = :showtimeId
        AND b.status IN ('PENDING', 'CONFIRMED')
    """)
    fun findBookedSeatIdsByShowtimeId(@Param("showtimeId") showtimeId: Long): List<Long>
}
