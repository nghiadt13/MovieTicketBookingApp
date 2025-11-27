package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ComboDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: Long,
    @JsonProperty("imageUrl")
    val imageUrl: String?
)
