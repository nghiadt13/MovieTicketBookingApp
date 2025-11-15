package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.News
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface NewsRepository : JpaRepository<News, Long> {

    @Query("SELECT n FROM News n ORDER BY n.publishedAt DESC")
    fun findTop10ByOrderByPublishedAtDesc(): List<News>
}
