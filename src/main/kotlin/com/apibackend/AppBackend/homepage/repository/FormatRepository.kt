package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.Format
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface FormatRepository : JpaRepository<Format, Long>
