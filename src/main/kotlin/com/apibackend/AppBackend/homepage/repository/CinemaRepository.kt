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

    @Query("SELECT c FROM Cinema c WHERE c.active = true ORDER BY c.name")
    fun findCinemasByMovieId(@Param("movieId") movieId: Long): List<Cinema>
}
