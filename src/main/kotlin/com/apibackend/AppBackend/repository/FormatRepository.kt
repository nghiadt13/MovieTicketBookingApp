package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.Format
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository interface FormatRepository : JpaRepository<Format, Long>
