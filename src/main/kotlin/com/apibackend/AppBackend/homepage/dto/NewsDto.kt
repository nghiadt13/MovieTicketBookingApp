package com.apibackend.AppBackend.homepage.dto

import java.time.LocalDateTime

data class NewsDto(
        val id: Long,
        val title: String,
        val content: String,
        val imageUrl: String?,
        val publishedAt: LocalDateTime
)
