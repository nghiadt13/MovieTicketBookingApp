package com.apibackend.AppBackend.model

import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Role(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Enumerated(EnumType.STRING) @Column(nullable = false, unique = true) val name: UserRole
)
