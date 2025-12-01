package com.apibackend.AppBackend.auth.repository

import com.apibackend.AppBackend.auth.model.OtpChannel
import com.apibackend.AppBackend.auth.model.OtpPurpose
import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.auth.model.UserOtp
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface UserOtpRepository : JpaRepository<UserOtp, Long> {

    @Query("""
        SELECT o FROM UserOtp o
        WHERE o.user = :user
        AND o.purpose = :purpose
        AND o.channel = :channel
        AND o.consumedAt IS NULL
        AND o.expiresAt > :now
        ORDER BY o.createdAt DESC
        LIMIT 1
    """)
    fun findActiveOtp(
        user: User,
        purpose: OtpPurpose,
        channel: OtpChannel,
        now: LocalDateTime
    ): Optional<UserOtp>

    @Query("""
        SELECT o FROM UserOtp o
        WHERE o.contactValue = :contactValue
        AND o.purpose = :purpose
        AND o.channel = :channel
        AND o.consumedAt IS NULL
        AND o.expiresAt > :now
        ORDER BY o.createdAt DESC
        LIMIT 1
    """)
    fun findActiveOtpByContact(
        contactValue: String,
        purpose: OtpPurpose,
        channel: OtpChannel,
        now: LocalDateTime
    ): Optional<UserOtp>

    @Query("""
        SELECT COUNT(o) FROM UserOtp o
        WHERE o.contactValue = :contactValue
        AND o.purpose = :purpose
        AND o.createdAt > :since
    """)
    fun countOtpsSentSince(
        contactValue: String,
        purpose: OtpPurpose,
        since: LocalDateTime
    ): Long

    @Query("""
        SELECT o FROM UserOtp o
        WHERE o.contactValue = :contactValue
        AND o.purpose = :purpose
        ORDER BY o.createdAt DESC
        LIMIT 1
    """)
    fun findLatestOtp(
        contactValue: String,
        purpose: OtpPurpose
    ): Optional<UserOtp>

    @Modifying
    @Query("""
        UPDATE UserOtp o
        SET o.consumedAt = :now
        WHERE o.user = :user
        AND o.purpose = :purpose
        AND o.consumedAt IS NULL
    """)
    fun invalidateAllOtps(user: User, purpose: OtpPurpose, now: LocalDateTime): Int

    @Modifying
    @Query("""
        DELETE FROM UserOtp o
        WHERE o.expiresAt < :threshold
        OR (o.consumedAt IS NOT NULL AND o.consumedAt < :threshold)
    """)
    fun deleteExpiredOtps(threshold: LocalDateTime): Int
}
