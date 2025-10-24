package com.apibackend.AppBackend.auth.service

import com.apibackend.AppBackend.auth.dto.LoginRequest
import com.apibackend.AppBackend.auth.dto.LoginResponse
import com.apibackend.AppBackend.auth.mapper.UserMapper
import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.auth.repository.UserRepository
import java.time.LocalDateTime
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(private val userRepository: UserRepository, private val userMapper: UserMapper) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun login(loginRequest: LoginRequest): LoginResponse {
        val user =
                findUserByUsername(loginRequest.username)
                        ?: return LoginResponse(
                                success = false,
                                message = "Invalid username or password"
                        )

        if (!user.isActive) {
            return LoginResponse(success = false, message = "Account is inactive")
        }

        if (!verifyPassword(loginRequest.password, user.passwordHash)) {
            return LoginResponse(success = false, message = "Invalid username or password")
        }

        user.lastLoginAt = LocalDateTime.now()
        userRepository.save(user)

        val userDto = userMapper.toDto(user)
        val token = "temporary-token-${user.id}"

        return LoginResponse(
                success = true,
                message = "Login successful",
                user = userDto,
                token = token
        )
    }

    private fun findUserByUsername(username: String): User? {
        val byEmail = userRepository.findByEmail(username)
        if (byEmail.isPresent) {
            return byEmail.get()
        }

        val byPhone = userRepository.findByPhoneNumber(username)
        return if (byPhone.isPresent) byPhone.get() else null
    }

    private fun verifyPassword(rawPassword: String, passwordHash: String?): Boolean {
        if (passwordHash == null) return false
        return try {
            passwordEncoder.matches(rawPassword, passwordHash)
        } catch (e: Exception) {
            false
        }
    }
}
