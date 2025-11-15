package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.Movie
import com.apibackend.AppBackend.homepage.model.MovieStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : JpaRepository<Movie, Long> {

        fun findByActiveTrue(): List<Movie>

        fun findByActiveTrueAndStatus(status: MovieStatus): List<Movie>

        fun findByActiveTrue(pageable: Pageable): Page<Movie>

        fun findByActiveTrueAndStatus(status: MovieStatus, pageable: Pageable): Page<Movie>

        fun findByStatus(status: MovieStatus, pageable: Pageable): Page<Movie>

        @Query(
                "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.active = true"
        )
        fun findAllActiveWithGenresAndFormats(): List<Movie>

        @Query(
                "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.active = true AND m.status = :status"
        )
        fun findByStatusWithGenresAndFormats(status: MovieStatus): List<Movie>

        @Query(
                "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.id = :id"
        )
        fun findByIdWithGenresAndFormats(id: Long): Movie?

        @Query(
                "SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats WHERE m.id = :id AND m.active = true"
        )
        fun findActiveByIdWithGenresAndFormats(id: Long): Movie?

        @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.genres LEFT JOIN FETCH m.formats")
        fun findAllWithGenresAndFormats(): List<Movie>

        @EntityGraph(attributePaths = ["genres", "formats"])
        fun findByIdIn(ids: List<Long>): List<Movie>
}
