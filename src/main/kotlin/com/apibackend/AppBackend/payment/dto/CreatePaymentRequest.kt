package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreatePaymentRequest(
    @field:NotNull(message = "Booking ID is required")
    @JsonProperty("bookingId")
    val bookingId: Long,

    @field:NotBlank(message = "Payment method ID is required")
    @JsonProperty("paymentMethodId")
    val paymentMethodId: String,

    @field:Valid
    val combos: List<ComboSelectionDto> = emptyList(),

    @JsonProperty("discountCode")
    val discountCode: String? = null
)

data class ComboSelectionDto(
    @field:NotNull(message = "Combo ID is required")
    @JsonProperty("comboId")
    val comboId: Long,

    @field:NotNull(message = "Quantity is required")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)
