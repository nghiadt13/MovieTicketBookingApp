package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.booking.model.TicketPrice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TicketPriceRepository : JpaRepository<TicketPrice, Long> {

    fun findByShowtimeId(showtimeId: Long): List<TicketPrice>

    @Query("""
        SELECT tp FROM TicketPrice tp
        WHERE tp.showtime.id IN :showtimeIds
    """)
    fun findByShowtimeIdIn(@Param("showtimeIds") showtimeIds: List<Long>): List<TicketPrice>
}
