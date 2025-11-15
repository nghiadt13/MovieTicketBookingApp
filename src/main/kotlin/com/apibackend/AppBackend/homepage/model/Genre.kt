package com.apibackend.AppBackend.homepage.model

import jakarta.persistence.*

@Entity
@Table(name = "genres")
data class Genre(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false, unique = true) val name: String,
        @Column(nullable = false, unique = true) val slug: String
)
