package com.apibackend.AppBackend.booking.repository

import com.apibackend.AppBackend.homepage.model.Showtime
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ShowtimeRepository : JpaRepository<Showtime, Long> {

    @Query(
            """
        SELECT s FROM Showtime s 
        JOIN FETCH s.movie m 
        JOIN FETCH s.screen sc 
        JOIN FETCH sc.cinema 
        WHERE s.id = :id
    """
    )
    fun findByIdWithDetails(@Param("id") id: Long): Showtime?

    fun findByMovieIdAndActiveTrue(movieId: Long): List<Showtime>
}
