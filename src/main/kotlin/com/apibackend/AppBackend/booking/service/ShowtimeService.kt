package com.apibackend.AppBackend.booking.service

import com.apibackend.AppBackend.booking.dto.ShowtimeDetailDto
import com.apibackend.AppBackend.booking.mapper.ShowtimeMapper
import com.apibackend.AppBackend.booking.repository.SeatRepository
import com.apibackend.AppBackend.booking.repository.ShowtimeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShowtimeService(
        private val showtimeRepository: ShowtimeRepository,
        private val seatRepository: SeatRepository,
        private val showtimeMapper: ShowtimeMapper
) {

    @Transactional(readOnly = true)
    fun getShowtimeDetail(showtimeId: Long): ShowtimeDetailDto? {
        val showtime = showtimeRepository.findByIdWithDetails(showtimeId) ?: return null

        // Fetch seats for the screen, ordered by row and seat number
        val seats = seatRepository.findByScreenIdOrderByRowNameAscSeatNumberAsc(showtime.screen.id)

        return showtimeMapper.toShowtimeDetailDto(showtime, seats)
    }
}
