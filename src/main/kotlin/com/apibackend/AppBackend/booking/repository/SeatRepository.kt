package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.booking.model.Seat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeatRepository : JpaRepository<Seat, Long> {
    fun findByScreenIdAndActiveTrue(screenId: Long): List<Seat>
    fun findByScreenIdOrderByRowNameAscSeatNumberAsc(screenId: Long): List<Seat>
}
