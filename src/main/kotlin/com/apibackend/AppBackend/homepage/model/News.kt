package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "news")
class News(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(nullable = false, columnDefinition = "TEXT") val title: String,
        @Column(nullable = false, columnDefinition = "TEXT") val content: String,
        @Column(name = "image_url", columnDefinition = "TEXT") val imageUrl: String? = null,
        @Column(name = "published_at") val publishedAt: LocalDateTime = LocalDateTime.now()
)
