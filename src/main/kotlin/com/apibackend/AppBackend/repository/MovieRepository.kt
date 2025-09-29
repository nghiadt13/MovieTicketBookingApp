package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.Movie
import com.apibackend.AppBackend.model.MovieStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Long> {

        fun findByActiveTrue(): List<Movie>

        fun findByActiveTrueAndStatus(status: MovieStatus): List<Movie>

        @Query(
                "SELECT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.active = true"
        )
        fun findAllActiveWithGenresAndFormats(): List<Movie>

        @Query(
                "SELECT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.active = true AND m.status = :status"
        )
        fun findByStatusWithGenresAndFormats(status: MovieStatus): List<Movie>

        @Query(
                "SELECT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.id = :id"
        )
        fun findByIdWithGenresAndFormats(id: Long): Movie?
}
