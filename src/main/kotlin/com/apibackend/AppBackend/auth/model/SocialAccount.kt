package com.apibackend.AppBackend.auth.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
        name = "social_accounts",
        uniqueConstraints =
                [
                        UniqueConstraint(columnNames = ["user_id", "provider"]),
                        UniqueConstraint(columnNames = ["provider", "provider_user_id"])]
)
class SocialAccount(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
        @Enumerated(EnumType.STRING) @Column(nullable = false) val provider: SocialProvider,
        @Column(name = "provider_user_id", nullable = false) val providerUserId: String,
        @Column(name = "created_at", nullable = false, updatable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
