package com.apibackend.AppBackend.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val info = Info()
            .title("Movie Ticket Backend API")
            .version("v1")
            .description(
                """
                REST API for managing movies, genres and formats.
                
                Highlights:
                - Soft delete using `is_active` (default filters active only)
                - Pagination via `/api/movies/paged` with standard `page`, `size`, `sort` params
                - Validation with detailed error responses
                - Hydration strategy: page base rows, then bulk-load associations
                
                Error model: ApiError { timestamp, status, error, message, path, fieldErrors }
                """.trimIndent()
            )
            .contact(Contact().name("AppBackend Team").email("team@example.com"))
            .license(License().name("Proprietary").url("https://example.com/license"))

        val servers = listOf(
            Server().url("http://localhost:8080").description("Local dev")
        )

        return OpenAPI()
            .info(info)
            .servers(servers)
            .tags(
                listOf(
                    Tag().name("Movies").description("Movie catalogue endpoints: list, paged, get, create, update, soft delete.")
                )
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Project Docs")
                    .url("https://example.com/docs")
            )
    }
}

