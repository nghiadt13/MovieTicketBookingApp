package com.apibackend.AppBackend.payment.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class BookingInfoDto(
    @JsonProperty("movieTitle")
    val movieTitle: String,

    @JsonProperty("moviePosterUrl")
    val moviePosterUrl: String?,

    val genre: String,

    val format: String,

    val duration: String,

    @JsonProperty("cinemaName")
    val cinemaName: String,

    val showtime: String,

    val showdate: String,

    val seats: List<String>,

    val room: String,

    @JsonProperty("ticketPrice")
    val ticketPrice: Long,

    @JsonProperty("ticketCount")
    val ticketCount: Int
)
