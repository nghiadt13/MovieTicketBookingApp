package com.apibackend.AppBackend.homepage.service

import com.apibackend.AppBackend.homepage.dto.NewsDto
import com.apibackend.AppBackend.homepage.mapper.NewsMapper
import com.apibackend.AppBackend.homepage.repository.NewsRepository
import org.springframework.stereotype.Service

@Service
class NewsService(private val newsRepository: NewsRepository, private val newsMapper: NewsMapper) {

    fun getLatest10News(): List<NewsDto> {
        return newsRepository.findTop10ByOrderByPublishedAtDesc().take(10).map {
            newsMapper.toDto(it)
        }
    }
}
