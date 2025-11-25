package com.apibackend.AppBackend.homepage.service

import com.apibackend.AppBackend.homepage.dto.CinemaDto
import com.apibackend.AppBackend.homepage.mapper.CinemaMapper
import com.apibackend.AppBackend.homepage.repository.CinemaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CinemaService(
        private val cinemaRepository: CinemaRepository,
        private val cinemaMapper: CinemaMapper
) {

    fun getAllActiveCinemas(): List<CinemaDto> {
        return cinemaMapper.cinemasToDtos(cinemaRepository.findAllActiveCinemas())
    }

    fun getCinemasByCity(city: String): List<CinemaDto> {
        return cinemaMapper.cinemasToDtos(cinemaRepository.findByActiveTrueAndCity(city))
    }

    fun getCinemasByMovieId(movieId: Long): List<CinemaDto> {
        return cinemaMapper.cinemasToDtos(cinemaRepository.findCinemasByMovieId(movieId))
    }
}
