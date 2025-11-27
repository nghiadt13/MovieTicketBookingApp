package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentMethodDto(
    val id: String,
    val name: String,
    val description: String?,
    @JsonProperty("iconType")
    val iconType: String
)
