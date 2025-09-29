package com.apibackend.AppBackend.model

import jakarta.persistence.*

@Entity
@Table(name = "formats")
data class Format(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(nullable = false, unique = true) val code: String,
        @Column(nullable = false) val label: String
)
