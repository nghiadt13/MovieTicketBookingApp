package com.apibackend.AppBackend.movies.repository

import com.apibackend.AppBackend.movies.model.Format
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface FormatRepository : JpaRepository<Format, Long>
