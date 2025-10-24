package com.apibackend.AppBackend.auth.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @Column(unique = true) val email: String? = null,
        @Column(name = "phone_number", unique = true) val phoneNumber: String? = null,
        @Column(name = "password_hash") var passwordHash: String? = null,
        @Column(name = "display_name", nullable = false) var displayName: String,
        @Column(name = "avatar_url") var avatarUrl: String? = null,
        @Column(name = "is_active", nullable = false) var isActive: Boolean = true,
        @Column(name = "email_verified_at") var emailVerifiedAt: LocalDateTime? = null,
        @Column(name = "phone_number_verified_at") var phoneNumberVerifiedAt: LocalDateTime? = null,
        @Column(name = "last_login_at") var lastLoginAt: LocalDateTime? = null,
        @Column(name = "created_at", nullable = false, updatable = false)
        val createdAt: LocalDateTime = LocalDateTime.now(),
        @Column(name = "updated_at", nullable = false)
        var updatedAt: LocalDateTime = LocalDateTime.now(),
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(
                name = "user_roles",
                joinColumns = [JoinColumn(name = "user_id")],
                inverseJoinColumns = [JoinColumn(name = "role_id")]
        )
        var roles: MutableSet<Role> = mutableSetOf(),
        @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
        var socialAccounts: MutableSet<SocialAccount> = mutableSetOf()
)
