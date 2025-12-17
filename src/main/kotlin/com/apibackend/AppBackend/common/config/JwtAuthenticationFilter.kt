package com.apibackend.AppBackend.common.config

import com.apibackend.AppBackend.auth.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter chạy trước mỗi request để validate JWT token
 * Nếu token hợp lệ → set Authentication vào SecurityContext
 */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // Bỏ qua nếu không có header hoặc không phải Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // Lấy token (bỏ prefix "Bearer ")
        val token = authHeader.substring(7)

        // Validate và chỉ chấp nhận access token (không phải refresh token)
        if (jwtService.validateToken(token) && jwtService.isAccessToken(token)) {
            val userId = jwtService.getUserIdFromToken(token)

            if (userId != null) {
                // Set authentication - principal là userId
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = UsernamePasswordAuthenticationToken(
                    userId,  // principal - có thể lấy bằng SecurityContextHolder
                    null,
                    authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
