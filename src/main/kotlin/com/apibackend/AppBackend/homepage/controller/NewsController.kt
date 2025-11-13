package com.apibackend.AppBackend.movies.controller

import com.apibackend.AppBackend.movies.dto.NewsDto
import com.apibackend.AppBackend.movies.service.NewsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/news") 
@Tag(name = "News", description = "News management APIs")
class NewsController(private val newsService: NewsService) {

    @GetMapping
    @Operation(
            summary = "Get latest 10 news",
            description = "Returns the 10 most recently updated active news items"
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successful retrieval")])
    fun getLatestNews(): ResponseEntity<List<NewsDto>> {
        val news = newsService.getLatest10News()
        return ResponseEntity.ok(news)
    }
}
