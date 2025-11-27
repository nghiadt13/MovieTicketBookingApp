package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentSummaryDto(
    @JsonProperty("ticketPrice")
    val ticketPrice: Long,

    @JsonProperty("ticketCount")
    val ticketCount: Int,

    @JsonProperty("comboPrice")
    val comboPrice: Long,

    val discount: Long,

    @JsonProperty("totalPrice")
    val totalPrice: Long
)
