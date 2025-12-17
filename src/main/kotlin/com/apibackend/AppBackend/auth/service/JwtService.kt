package com.apibackend.AppBackend.auth.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

/**
 * Service xử lý JWT token (tạo, validate, parse)
 * Sử dụng HMAC-SHA256 để sign token
 */
@Service
class JwtService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.access-token-expiry-ms}") private val accessTokenExpiryMs: Long,  // 1 giờ
    @Value("\${jwt.refresh-token-expiry-ms}") private val refreshTokenExpiryMs: Long // 7 ngày
) {
    // Lazy init secret key từ config
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    /** Tạo access token - chứa userId, email, roles. Dùng cho API calls */
    fun generateAccessToken(userId: Long, email: String?, roles: List<String>): String {
        return generateToken(userId, email, roles, accessTokenExpiryMs, "access")
    }

    /** Tạo refresh token - chỉ chứa userId. Dùng để lấy access token mới */
    fun generateRefreshToken(userId: Long): String {
        return generateToken(userId, null, emptyList(), refreshTokenExpiryMs, "refresh")
    }

    /** Helper: tạo JWT với claims tùy chỉnh */
    private fun generateToken(
        userId: Long,
        email: String?,
        roles: List<String>,
        expiryMs: Long,
        tokenType: String
    ): String {
        val now = Date()
        val expiry = Date(now.time + expiryMs)

        val builder = Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .claim("type", tokenType)

        if (email != null) {
            builder.claim("email", email)
        }
        if (roles.isNotEmpty()) {
            builder.claim("roles", roles)
        }

        return builder.signWith(secretKey).compact()
    }

    /** Kiểm tra token hợp lệ (chưa hết hạn, signature đúng) */
    fun validateToken(token: String): Boolean {
        return try {
            parseToken(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Lấy userId từ token (subject claim) */
    fun getUserIdFromToken(token: String): Long? {
        return try {
            val claims = parseToken(token)
            claims.subject?.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun getTokenType(token: String): String? {
        return try {
            val claims = parseToken(token)
            claims["type"] as? String
        } catch (e: Exception) {
            null
        }
    }

    fun isAccessToken(token: String): Boolean {
        return getTokenType(token) == "access"
    }

    fun isRefreshToken(token: String): Boolean {
        return getTokenType(token) == "refresh"
    }

    private fun parseToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun getAccessTokenExpirySeconds(): Int = (accessTokenExpiryMs / 1000).toInt()
}
