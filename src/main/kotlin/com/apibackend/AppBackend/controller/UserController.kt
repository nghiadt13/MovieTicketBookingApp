package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.dto.UserDto
import com.apibackend.AppBackend.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints")
class UserController(private val userService: UserService) {

    @GetMapping
    @Operation(
            summary = "List all users",
            description = "Returns all users. Use activeOnly parameter to filter active users only."
    )
    fun getAllUsers(
            @Parameter(description = "Return only active users")
            @RequestParam(required = false, defaultValue = "true")
            activeOnly: Boolean
    ): ResponseEntity<List<UserDto>> {
        val users =
                if (activeOnly) {
                    userService.getAllActiveUsers()
                } else {
                    userService.getAllUsers()
                }
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user by id",
            description = "Returns a single user by id with their roles"
    )
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
