package com.apibackend.AppBackend.auth.service

import com.apibackend.AppBackend.auth.dto.*
import com.apibackend.AppBackend.auth.mapper.UserMapper
import com.apibackend.AppBackend.auth.model.User
import com.apibackend.AppBackend.auth.model.UserRole
import com.apibackend.AppBackend.auth.repository.RoleRepository
import com.apibackend.AppBackend.auth.repository.UserRepository
import java.time.LocalDateTime
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val userMapper: UserMapper,
    private val jwtService: JwtService
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun login(loginRequest: LoginRequest): LoginResponse {
        val user = findUserByUsername(loginRequest.username)
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
        val roles = user.roles.map { it.name.name }
        val accessToken = jwtService.generateAccessToken(user.id!!, user.email, roles)

        return LoginResponse(
            success = true,
            message = "Login successful",
            user = userDto,
            token = accessToken
        )
    }

    fun findUserByUsername(username: String): User? {
        val byEmail = userRepository.findByEmail(username)
        if (byEmail.isPresent) {
            return byEmail.get()
        }

        val byPhone = userRepository.findByPhoneNumber(username)
        return if (byPhone.isPresent) byPhone.get() else null
    }

    fun verifyPassword(rawPassword: String, passwordHash: String?): Boolean {
        if (passwordHash == null) return false
        return try {
            passwordEncoder.matches(rawPassword, passwordHash)
        } catch (e: Exception) {
            false
        }
    }

    fun hashPassword(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    /**
     * Đăng ký tài khoản mới
     * - Validate email/phone chưa tồn tại
     * - Hash password
     * - Gán role USER mặc định
     * - Auto login sau đăng ký (trả về token)
     */
    fun register(request: RegisterRequest): RegisterResponse {
        // Validate: phải có email hoặc phone
        if (request.email.isNullOrBlank() && request.phoneNumber.isNullOrBlank()) {
            return RegisterResponse(
                success = false,
                message = "Email or phone number is required"
            )
        }

        // Check email đã tồn tại
        if (!request.email.isNullOrBlank()) {
            if (userRepository.findByEmail(request.email).isPresent) {
                return RegisterResponse(
                    success = false,
                    message = "Email already registered"
                )
            }
        }

        // Check phone đã tồn tại
        val normalizedPhone = request.phoneNumber?.let { normalizePhoneNumber(it) }
        if (!normalizedPhone.isNullOrBlank()) {
            if (userRepository.findByPhoneNumber(normalizedPhone).isPresent) {
                return RegisterResponse(
                    success = false,
                    message = "Phone number already registered"
                )
            }
        }

        // Lấy role USER mặc định (truyền String để native query cast sang enum)
        val userRole = roleRepository.findRoleByNameString(UserRole.ROLE_USER.name).orElse(null)
            ?: return RegisterResponse(
                success = false,
                message = "System error: Default role not found"
            )

        // Tạo user mới
        val newUser = User(
            email = request.email,
            phoneNumber = normalizedPhone,
            passwordHash = hashPassword(request.password),
            displayName = request.displayName,
            isActive = true,
            lastLoginAt = LocalDateTime.now()
        )
        newUser.roles.add(userRole)

        val savedUser = userRepository.save(newUser)

        // Tạo token (auto login)
        val userDto = userMapper.toDto(savedUser)
        val roles = savedUser.roles.map { it.name.name }
        val accessToken = jwtService.generateAccessToken(savedUser.id!!, savedUser.email, roles)

        return RegisterResponse(
            success = true,
            message = "Registration successful",
            user = userDto,
            token = accessToken
        )
    }

    /** Chuẩn hóa số điện thoại về format +84... */
    private fun normalizePhoneNumber(phone: String): String {
        var normalized = phone.replace(Regex("[\\s\\-\\(\\)]"), "")
        if (normalized.startsWith("0")) {
            normalized = "+84" + normalized.substring(1)
        }
        if (!normalized.startsWith("+")) {
            normalized = "+$normalized"
        }
        return normalized
    }
}
