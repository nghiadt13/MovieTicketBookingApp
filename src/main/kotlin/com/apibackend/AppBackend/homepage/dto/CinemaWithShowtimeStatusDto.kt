package com.apibackend.AppBackend.homepage.dto

import java.math.BigDecimal

data class CinemaWithShowtimeStatusDto(
        val id: Long,
        val name: String,
        val address: String,
        val city: String,
        val district: String?,
        val phoneNumber: String?,
        val email: String?,
        val latitude: BigDecimal?,
        val longitude: BigDecimal?,
        val imageUrl: String?,
        val hasShowtimes: Boolean,
        val message: String? = null
)
