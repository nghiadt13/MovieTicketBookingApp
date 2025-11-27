package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.homepage.model.Showtime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ShowtimeRepository : JpaRepository<Showtime, Long> {

    @Query("""
        SELECT s FROM Showtime s
        JOIN FETCH s.movie m
        JOIN FETCH s.screen sc
        JOIN FETCH sc.cinema
        LEFT JOIN FETCH s.format
        WHERE s.id = :id
    """)
    fun findByIdWithDetails(@Param("id") id: Long): Showtime?

    fun findByMovieIdAndActiveTrue(movieId: Long): List<Showtime>

    /**
     * Find all showtimes for a movie at a specific cinema
     * Used for unified booking data API
     */
    @Query("""
        SELECT DISTINCT s FROM Showtime s
        JOIN FETCH s.screen sc
        LEFT JOIN FETCH s.format f
        WHERE s.movie.id = :movieId
        AND sc.cinema.id = :cinemaId
        AND s.active = true
        AND sc.active = true
        AND s.startTime >= :fromTime
        ORDER BY s.startTime
    """)
    fun findShowtimesForBooking(
        @Param("movieId") movieId: Long,
        @Param("cinemaId") cinemaId: Long,
        @Param("fromTime") fromTime: OffsetDateTime = OffsetDateTime.now()
    ): List<Showtime>

    /**
     * Get distinct available dates for a movie at a cinema
     */
    @Query("""
        SELECT DISTINCT CAST(s.startTime AS date) FROM Showtime s
        JOIN s.screen sc
        WHERE s.movie.id = :movieId
        AND sc.cinema.id = :cinemaId
        AND s.active = true
        AND sc.active = true
        AND s.startTime >= :fromTime
        ORDER BY 1
    """)
    fun findAvailableDates(
        @Param("movieId") movieId: Long,
        @Param("cinemaId") cinemaId: Long,
        @Param("fromTime") fromTime: OffsetDateTime = OffsetDateTime.now()
    ): List<java.time.LocalDate>

    /**
     * Get available formats for a movie at a cinema
     */
    @Query("""
        SELECT DISTINCT f.id, f.code, f.label FROM Showtime s
        JOIN s.screen sc
        JOIN s.format f
        WHERE s.movie.id = :movieId
        AND sc.cinema.id = :cinemaId
        AND s.active = true
        AND sc.active = true
        AND s.startTime >= :fromTime
    """)
    fun findAvailableFormats(
        @Param("movieId") movieId: Long,
        @Param("cinemaId") cinemaId: Long,
        @Param("fromTime") fromTime: OffsetDateTime = OffsetDateTime.now()
    ): List<Array<Any>>
}
