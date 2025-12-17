package com.apibackend.AppBackend.auth.controller

import com.apibackend.AppBackend.auth.dto.*
import com.apibackend.AppBackend.auth.service.AuthService
import com.apibackend.AppBackend.common.config.ApiError
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description =
                    "Authenticate user with email/phone and password. Returns user info and token on success."
    )
    @ApiResponses(
            value =
                    [
                            ApiResponse(
                                    responseCode = "200",
                                    description = "Login successful or failed",
                                    content =
                                            [
                                                    Content(
                                                            schema =
                                                                    Schema(
                                                                            implementation =
                                                                                    LoginResponse::class
                                                                    )
                                                    )]
                            ),
                            ApiResponse(
                                    responseCode = "400",
                                    description = "Validation error",
                                    content =
                                            [
                                                    Content(
                                                            schema =
                                                                    Schema(
                                                                            implementation =
                                                                                    ApiError::class
                                                                    )
                                                    )]
                            )]
    )
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(loginRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    @Operation(
        summary = "User registration",
        description = "Register new user with email/phone and password. Auto login on success."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Registration successful or failed",
                content = [Content(schema = Schema(implementation = RegisterResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }
}
