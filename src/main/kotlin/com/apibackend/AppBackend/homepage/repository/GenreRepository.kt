package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.Genre
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface GenreRepository : JpaRepository<Genre, Long>
