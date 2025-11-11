package com.apibackend.AppBackend.movies.mapper

import com.apibackend.AppBackend.movies.dto.NewsDto
import com.apibackend.AppBackend.movies.model.News
import org.springframework.stereotype.Component

@Component
class NewsMapper {

    fun toDto(news: News): NewsDto {
        return NewsDto(
                id = news.id!!,
                title = news.title,
                content = news.content,
                imageUrl = news.imageUrl,
                author = news.author,
                isActive = news.isActive,
                createdAt = news.createdAt,
                updatedAt = news.updatedAt
        )
    }
}
