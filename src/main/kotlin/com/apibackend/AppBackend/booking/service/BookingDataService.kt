package com.apibackend.AppBackend.booking.service

import com.apibackend.AppBackend.booking.dto.*
import com.apibackend.AppBackend.booking.model.SeatType
import com.apibackend.AppBackend.booking.repository.*
import com.apibackend.AppBackend.common.exception.ResourceNotFoundException
import com.apibackend.AppBackend.homepage.model.Format
import com.apibackend.AppBackend.homepage.model.Showtime
import com.apibackend.AppBackend.homepage.repository.CinemaRepository
import com.apibackend.AppBackend.homepage.repository.MovieRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Service
class BookingDataService(
    private val movieRepository: MovieRepository,
    private val cinemaRepository: CinemaRepository,
    private val showtimeRepository: ShowtimeRepository,
    private val seatRepository: SeatRepository,
    private val ticketPriceRepository: TicketPriceRepository,
    private val bookingItemRepository: BookingItemRepository,
    private val seatLockRepository: SeatLockRepository
) {

    /**
     * Get unified booking data for a movie at a specific cinema
     */
    @Transactional(readOnly = true)
    fun getBookingData(movieId: Long, cinemaId: Long): BookingDataResponse {
        // 1. Get movie with genres
        val movie = movieRepository.findActiveByIdWithGenresAndFormats(movieId)
            ?: throw ResourceNotFoundException("Movie not found with id: $movieId")

        // 2. Get cinema
        val cinema = cinemaRepository.findById(cinemaId)
            .orElseThrow { ResourceNotFoundException("Cinema not found with id: $cinemaId") }

        // 3. Get all showtimes for this movie at this cinema
        val showtimes = showtimeRepository.findShowtimesForBooking(movieId, cinemaId)

        if (showtimes.isEmpty()) {
            return BookingDataResponse(
                movie = toMovieInfoDto(movie),
                cinema = toCinemaInfoDto(cinema),
                availableDates = emptyList(),
                formats = emptyList(),
                showtimes = emptyMap()
            )
        }

        // 4. Get ticket prices for all showtimes
        val showtimeIds = showtimes.map { it.id }
        val ticketPrices = ticketPriceRepository.findByShowtimeIdIn(showtimeIds)
        val pricesByShowtime = ticketPrices.groupBy { it.showtime.id }

        // 5. Build available dates
        val availableDates = buildAvailableDates(showtimes)

        // 6. Build available formats
        val formats = buildFormats(showtimes)

        // 7. Group showtimes by date and format
        val groupedShowtimes = buildGroupedShowtimes(showtimes, pricesByShowtime)

        return BookingDataResponse(
            movie = toMovieInfoDto(movie),
            cinema = toCinemaInfoDto(cinema),
            availableDates = availableDates,
            formats = formats,
            showtimes = groupedShowtimes
        )
    }

    /**
     * Get seat map for a specific showtime
     */
    @Transactional(readOnly = true)
    fun getSeatMap(showtimeId: Long): SeatMapResponse {
        val showtime = showtimeRepository.findByIdWithDetails(showtimeId)
            ?: throw ResourceNotFoundException("Showtime not found with id: $showtimeId")

        val screen = showtime.screen
        val seats = seatRepository.findByScreenIdOrderByRowNameAscSeatNumberAsc(screen.id)

        // Get booked seat IDs
        val bookedSeatIds = bookingItemRepository.findBookedSeatIdsByShowtimeId(showtimeId).toSet()

        // Get locked seat IDs
        val lockedSeatIds = seatLockRepository.findLockedSeatIdsForShowtime(showtimeId).toSet()

        // Get ticket prices for this showtime
        val ticketPrices = ticketPriceRepository.findByShowtimeId(showtimeId)
        val priceMap = ticketPrices.associate { it.seatType to it.price }

        // Calculate grid dimensions
        val rows = seats.map { it.rowName }.distinct().size
        val columns = seats.maxOfOrNull { it.seatNumber.toInt() } ?: 0

        val seatInfoList = seats.filter { it.active }.map { seat ->
            val status = when {
                bookedSeatIds.contains(seat.id) -> SeatStatus.BOOKED
                lockedSeatIds.contains(seat.id) -> SeatStatus.LOCKED
                else -> SeatStatus.AVAILABLE
            }

            SeatInfoDto(
                id = "${seat.rowName}${seat.seatNumber}",
                seatId = seat.id,
                row = rowToIndex(seat.rowName),
                rowLabel = seat.rowName,
                column = seat.seatNumber.toInt() - 1,
                columnLabel = seat.seatNumber.toString(),
                status = status,
                type = seat.seatType,
                price = priceMap[seat.seatType] ?: getDefaultPrice(seat.seatType)
            )
        }

        return SeatMapResponse(
            showtimeId = showtimeId,
            screenId = screen.id,
            screenName = screen.name,
            rows = rows,
            columns = columns,
            seats = seatInfoList,
            prices = priceMap.ifEmpty { getDefaultPrices() }
        )
    }

    // ===== Private helper methods =====

    private fun toMovieInfoDto(movie: com.apibackend.AppBackend.homepage.model.Movie): MovieInfoDto {
        return MovieInfoDto(
            id = movie.id,
            title = movie.title,
            posterUrl = movie.posterUrl,
            genres = movie.genres.map { it.name },
            duration = movie.durationMin?.toInt(),
            durationFormatted = movie.durationMin?.let { formatDuration(it.toInt()) },
            ratingAvg = movie.ratingAvg
        )
    }

    private fun toCinemaInfoDto(cinema: com.apibackend.AppBackend.homepage.model.Cinema): CinemaInfoDto {
        return CinemaInfoDto(
            id = cinema.id,
            name = cinema.name,
            address = cinema.address,
            city = cinema.city,
            imageUrl = cinema.imageUrl
        )
    }

    private fun buildAvailableDates(showtimes: List<Showtime>): List<AvailableDateDto> {
        val today = LocalDate.now()
        val dates = showtimes
            .map { it.startTime.toLocalDate() }
            .distinct()
            .sorted()

        return dates.map { date ->
            AvailableDateDto(
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                dayOfWeekShort = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                dayNumber = date.dayOfMonth,
                monthShort = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                isToday = date == today
            )
        }
    }

    private fun buildFormats(showtimes: List<Showtime>): List<ScreeningFormatDto> {
        val formats = showtimes
            .mapNotNull { it.format }
            .distinctBy { it.id }

        if (formats.isEmpty()) {
            // Return default format if no formats are assigned
            return listOf(
                ScreeningFormatDto(
                    id = 0,
                    name = "Standard",
                    code = "STANDARD",
                    isDefault = true
                )
            )
        }

        return formats.mapIndexed { index, format ->
            ScreeningFormatDto(
                id = format.id,
                name = format.label,
                code = format.code,
                isDefault = index == 0
            )
        }
    }

    private fun buildGroupedShowtimes(
        showtimes: List<Showtime>,
        pricesByShowtime: Map<Long, List<com.apibackend.AppBackend.booking.model.TicketPrice>>
    ): Map<String, Map<String, List<ShowtimeSlotDto>>> {
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        return showtimes.groupBy { showtime ->
            showtime.startTime.toLocalDate().format(dateFormatter)
        }.mapValues { (_, dateShowtimes) ->
            dateShowtimes.groupBy { showtime ->
                showtime.format?.code ?: "STANDARD"
            }.mapValues { (_, formatShowtimes) ->
                formatShowtimes.map { showtime ->
                    val prices = pricesByShowtime[showtime.id]
                        ?.associate { it.seatType to it.price }
                        ?: getDefaultPrices()

                    val totalSeats = showtime.screen.totalSeats.toInt()
                    val availableSeats = showtime.availableSeats?.toInt() ?: totalSeats
                    val occupancyRate = 1 - (availableSeats.toDouble() / totalSeats)

                    ShowtimeSlotDto(
                        id = showtime.id,
                        time = showtime.startTime.format(timeFormatter),
                        startTime = showtime.startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        endTime = showtime.endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        screenId = showtime.screen.id,
                        screenName = showtime.screen.name,
                        formatCode = showtime.format?.code,
                        availableSeats = availableSeats,
                        totalSeats = totalSeats,
                        prices = prices,
                        isAlmostFull = occupancyRate >= 0.8,
                        isSoldOut = availableSeats == 0
                    )
                }
            }
        }
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }

    private fun rowToIndex(rowName: String): Int {
        // Convert row label to 0-indexed number (A=0, B=1, etc.)
        return if (rowName.length == 1 && rowName[0].isLetter()) {
            rowName[0].uppercaseChar() - 'A'
        } else {
            rowName.hashCode() % 26
        }
    }

    private fun getDefaultPrice(seatType: SeatType): BigDecimal {
        return when (seatType) {
            SeatType.STANDARD -> BigDecimal(85000)
            SeatType.VIP -> BigDecimal(110000)
            SeatType.COUPLE -> BigDecimal(200000)
            SeatType.DELUXE -> BigDecimal(150000)
        }
    }

    private fun getDefaultPrices(): Map<SeatType, BigDecimal> {
        return mapOf(
            SeatType.STANDARD to BigDecimal(85000),
            SeatType.VIP to BigDecimal(110000),
            SeatType.COUPLE to BigDecimal(200000),
            SeatType.DELUXE to BigDecimal(150000)
        )
    }
}
