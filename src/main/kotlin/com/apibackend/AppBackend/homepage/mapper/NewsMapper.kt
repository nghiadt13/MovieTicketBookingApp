package com.apibackend.AppBackend.homepage.mapper

import com.apibackend.AppBackend.homepage.dto.NewsDto
import com.apibackend.AppBackend.homepage.model.News
import org.springframework.stereotype.Component

@Component
class NewsMapper {

    fun toDto(news: News): NewsDto {
        return NewsDto(
                id = news.id!!,
                title = news.title,
                content = news.content,
                imageUrl = news.imageUrl,
                publishedAt = news.publishedAt
        )
    }
}
