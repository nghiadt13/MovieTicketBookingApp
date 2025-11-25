package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.Cinema
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CinemaRepository : JpaRepository<Cinema, Long> {

    @Query("SELECT c FROM Cinema c WHERE c.active = true ORDER BY c.name")
    fun findAllActiveCinemas(): List<Cinema>

    fun findByActiveTrueAndCity(city: String): List<Cinema>

    @Query(
            """
        SELECT DISTINCT c FROM Cinema c
        JOIN Screen s ON s.cinema.id = c.id
        JOIN Showtime st ON st.screen.id = s.id
        WHERE st.movie.id = :movieId
        AND c.active = true
        AND s.active = true
        AND st.active = true
        AND st.startTime >= CURRENT_TIMESTAMP
        ORDER BY c.name
    """
    )
    fun findCinemasByMovieId(@Param("movieId") movieId: Long): List<Cinema>
}
