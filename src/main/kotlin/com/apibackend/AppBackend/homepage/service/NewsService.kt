package com.apibackend.AppBackend.movies.service

import com.apibackend.AppBackend.movies.dto.NewsDto
import com.apibackend.AppBackend.movies.mapper.NewsMapper
import com.apibackend.AppBackend.movies.repository.NewsRepository
import org.springframework.stereotype.Service

@Service
class NewsService(private val newsRepository: NewsRepository, private val newsMapper: NewsMapper) {

    fun getLatest10News(): List<NewsDto> {
        return newsRepository.findTop10ByOrderByPublishedAtDesc().take(10).map {
            newsMapper.toDto(it)
        }
    }
}
