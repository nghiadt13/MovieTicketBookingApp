package com.apibackend.AppBackend.homepage.service

import com.apibackend.AppBackend.booking.repository.ShowtimeRepository
import com.apibackend.AppBackend.homepage.dto.CinemaDto
import com.apibackend.AppBackend.homepage.dto.CinemaWithShowtimeStatusDto
import com.apibackend.AppBackend.homepage.mapper.CinemaMapper
import com.apibackend.AppBackend.homepage.repository.CinemaRepository
import java.time.OffsetDateTime
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CinemaService(
        private val cinemaRepository: CinemaRepository,
        private val cinemaMapper: CinemaMapper,
        private val showtimeRepository: ShowtimeRepository
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

    fun getCinemasWithShowtimeStatus(movieId: Long): List<CinemaWithShowtimeStatusDto> {
        val cinemas = cinemaRepository.findAllActiveCinemas()

        return cinemas.map { cinema ->
            val showtimes =
                    showtimeRepository.findShowtimesForBooking(
                            movieId = movieId,
                            cinemaId = cinema.id,
                            fromTime = OffsetDateTime.now()
                    )
            val hasShowtimes = showtimes.isNotEmpty()

            CinemaWithShowtimeStatusDto(
                    id = cinema.id,
                    name = cinema.name,
                    address = cinema.address,
                    city = cinema.city,
                    district = cinema.district,
                    phoneNumber = cinema.phoneNumber,
                    email = cinema.email,
                    latitude = cinema.latitude,
                    longitude = cinema.longitude,
                    imageUrl = cinema.imageUrl,
                    hasShowtimes = hasShowtimes,
                    message =
                            if (!hasShowtimes)
                                    "Hiện tại không có suất chiếu nào cho phim này tại rạp"
                            else null
            )
        }
    }
}
