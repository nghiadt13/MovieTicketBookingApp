package com.apibackend.AppBackend.homepage.dto

import java.math.BigDecimal

data class CinemaDto(
        val id: Long,
        val name: String,
        val address: String,
        val city: String,
        val district: String?,
        val phoneNumber: String?,
        val email: String?,
        val latitude: BigDecimal?,
        val longitude: BigDecimal?
)
