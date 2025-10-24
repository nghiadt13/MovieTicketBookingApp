package com.apibackend.AppBackend.movies.dto

import java.time.LocalDateTime

data class CarouselItemDto(
        val id: Long,
        val title: String,
        val imageUrl: String,
        val content: String?,
        val targetUrl: String?,
        val isActive: Boolean,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
