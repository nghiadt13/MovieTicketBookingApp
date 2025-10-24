package com.apibackend.AppBackend.movies.repository

import com.apibackend.AppBackend.movies.model.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface GenreRepository : JpaRepository<Genre, Long>
