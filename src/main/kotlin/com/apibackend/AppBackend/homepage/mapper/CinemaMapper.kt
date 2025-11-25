package com.apibackend.AppBackend.homepage.mapper

import com.apibackend.AppBackend.homepage.dto.CinemaDto
import com.apibackend.AppBackend.homepage.model.Cinema
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface CinemaMapper {

    fun cinemaToDto(cinema: Cinema): CinemaDto

    fun cinemasToDtos(cinemas: List<Cinema>): List<CinemaDto>
}
