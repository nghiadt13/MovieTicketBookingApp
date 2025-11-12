package com.apibackend.AppBackend.homepage.dto

data class MembershipTierDto(
        val id: Long,
        val name: String,
        val imageUrl: String?,
        val description: String?
)
