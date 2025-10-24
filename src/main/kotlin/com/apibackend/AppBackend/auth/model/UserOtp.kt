package com.apibackend.AppBackend.auth.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_otps")
class UserOtp(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        val user: User,
        @Enumerated(EnumType.STRING) @Column(nullable = false) val purpose: OtpPurpose,
        @Enumerated(EnumType.STRING) @Column(nullable = false) val channel: OtpChannel,
        @Column(name = "contact_value", nullable = false) val contactValue: String,
        @Column(name = "code_hash", nullable = false) val codeHash: String,
        @Column(name = "expires_at", nullable = false) val expiresAt: LocalDateTime,
        @Column(name = "consumed_at") var consumedAt: LocalDateTime? = null,
        @Column(name = "attempt_count", nullable = false) var attemptCount: Short = 0,
        @Column(name = "created_at", nullable = false, updatable = false)
        val createdAt: LocalDateTime = LocalDateTime.now()
)
