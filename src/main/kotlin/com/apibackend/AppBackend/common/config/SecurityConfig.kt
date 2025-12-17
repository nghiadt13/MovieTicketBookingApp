package com.apibackend.AppBackend.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Cấu hình Spring Security:
 * - Stateless session (không dùng cookie/session)
 * - JWT filter để authenticate
 * - CORS cho mobile app
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    companion object {
        // Các endpoint không cần đăng nhập
        val PUBLIC_ENDPOINTS = arrayOf(
            "/api/auth/**",
            "/api/movies/**",
            "/api/carousel/**",
            "/api/news/**",
            "/api/membership-tiers/**",
            "/api/cinemas/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
        )
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*PUBLIC_ENDPOINTS).permitAll()  // Ai cũng truy cập được
                    .requestMatchers("/api/users/me/**").authenticated()  // Cần đăng nhập
                    .requestMatchers("/api/booking/**", "/api/bookings/**").authenticated()
                    .requestMatchers("/api/payments/**").authenticated()
                    .requestMatchers("/api/reviews/**").authenticated()
                    .anyRequest().permitAll()
            }
            // Thêm JWT filter trước filter mặc định của Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:8080",
            "http://10.0.2.2:8080"
        )
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
