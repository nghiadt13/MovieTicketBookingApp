package com.apibackend.AppBackend.movies.dto

import java.time.LocalDateTime

data class NewsDto(
        val id: Long,
        val title: String,
        val content: String?,
        val imageUrl: String?,
        val author: String?,
        val isActive: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
