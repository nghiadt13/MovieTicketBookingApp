package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.booking.model.SeatLock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface SeatLockRepository : JpaRepository<SeatLock, Long> {

    @Query("""
        SELECT sl FROM SeatLock sl
        WHERE sl.showtime.id = :showtimeId
        AND sl.lockedUntil > :now
    """)
    fun findActiveLocksForShowtime(
        @Param("showtimeId") showtimeId: Long,
        @Param("now") now: OffsetDateTime = OffsetDateTime.now()
    ): List<SeatLock>

    @Query("""
        SELECT sl.seat.id FROM SeatLock sl
        WHERE sl.showtime.id = :showtimeId
        AND sl.lockedUntil > :now
    """)
    fun findLockedSeatIdsForShowtime(
        @Param("showtimeId") showtimeId: Long,
        @Param("now") now: OffsetDateTime = OffsetDateTime.now()
    ): List<Long>

    @Query("""
        SELECT sl FROM SeatLock sl
        WHERE sl.showtime.id = :showtimeId
        AND sl.seat.id IN :seatIds
        AND sl.lockedUntil > :now
    """)
    fun findLocksForSeats(
        @Param("showtimeId") showtimeId: Long,
        @Param("seatIds") seatIds: List<Long>,
        @Param("now") now: OffsetDateTime = OffsetDateTime.now()
    ): List<SeatLock>

    @Query("""
        SELECT sl FROM SeatLock sl
        WHERE sl.showtime.id = :showtimeId
        AND sl.user.id = :userId
        AND sl.lockedUntil > :now
    """)
    fun findUserLocksForShowtime(
        @Param("showtimeId") showtimeId: Long,
        @Param("userId") userId: Long,
        @Param("now") now: OffsetDateTime = OffsetDateTime.now()
    ): List<SeatLock>

    @Modifying
    @Query("""
        DELETE FROM SeatLock sl
        WHERE sl.lockedUntil < :now
    """)
    fun deleteExpiredLocks(@Param("now") now: OffsetDateTime = OffsetDateTime.now()): Int

    @Modifying
    @Query("""
        DELETE FROM SeatLock sl
        WHERE sl.showtime.id = :showtimeId
        AND sl.user.id = :userId
    """)
    fun deleteUserLocksForShowtime(
        @Param("showtimeId") showtimeId: Long,
        @Param("userId") userId: Long
    ): Int
}
