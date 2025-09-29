package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface GenreRepository : JpaRepository<Genre, Long>
