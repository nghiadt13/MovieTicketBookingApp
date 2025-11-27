package com.apibackend.AppBackend.booking.service

import com.apibackend.AppBackend.auth.repository.UserRepository
import com.apibackend.AppBackend.booking.dto.*
import com.apibackend.AppBackend.booking.model.*
import com.apibackend.AppBackend.booking.repository.*
import com.apibackend.AppBackend.common.exception.BadRequestException
import com.apibackend.AppBackend.common.exception.ConflictException
import com.apibackend.AppBackend.common.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val bookingItemRepository: BookingItemRepository,
    private val showtimeRepository: ShowtimeRepository,
    private val seatRepository: SeatRepository,
    private val seatLockRepository: SeatLockRepository,
    private val ticketPriceRepository: TicketPriceRepository,
    private val userRepository: UserRepository
) {

    companion object {
        private const val BOOKING_EXPIRY_MINUTES = 15L
    }

    /**
     * Create a new booking
     */
    @Transactional
    fun createBooking(userId: Long, request: CreateBookingRequest): BookingResponseDto {
        // 1. Validate user exists
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        // 2. Validate showtime exists and is active
        val showtime = showtimeRepository.findByIdWithDetails(request.showtimeId)
            ?: throw ResourceNotFoundException("Showtime not found with id: ${request.showtimeId}")

        if (!showtime.active) {
            throw BadRequestException("Showtime is not available")
        }

        if (showtime.startTime.isBefore(OffsetDateTime.now())) {
            throw BadRequestException("Cannot book for past showtimes")
        }

        // 3. Validate seats
        if (request.seatIds.isEmpty()) {
            throw BadRequestException("At least one seat must be selected")
        }

        val seats = seatRepository.findAllById(request.seatIds)
        if (seats.size != request.seatIds.size) {
            throw BadRequestException("Some seats were not found")
        }

        // Verify all seats belong to the same screen as the showtime
        val screenId = showtime.screen.id
        val invalidSeats = seats.filter { it.screen.id != screenId }
        if (invalidSeats.isNotEmpty()) {
            throw BadRequestException("Some seats do not belong to this screen")
        }

        // 4. Check seat availability (not booked or locked by others)
        val bookedSeatIds = bookingItemRepository.findBookedSeatIdsByShowtimeId(request.showtimeId).toSet()
        val lockedSeatIds = seatLockRepository.findLockedSeatIdsForShowtime(request.showtimeId)
            .toSet()

        // Get current user's locks (they can book seats they've locked)
        val userLockedSeatIds = seatLockRepository.findUserLocksForShowtime(request.showtimeId, userId)
            .map { it.seat.id }
            .toSet()

        val unavailableSeatIds = request.seatIds.filter { seatId ->
            bookedSeatIds.contains(seatId) ||
                    (lockedSeatIds.contains(seatId) && !userLockedSeatIds.contains(seatId))
        }

        if (unavailableSeatIds.isNotEmpty()) {
            throw ConflictException("Some seats are no longer available: ${unavailableSeatIds.joinToString(", ")}")
        }

        // 5. Calculate total price
        val ticketPrices = ticketPriceRepository.findByShowtimeId(request.showtimeId)
        val priceMap = ticketPrices.associate { it.seatType to it.price }

        val calculatedTotal = seats.sumOf { seat ->
            priceMap[seat.seatType] ?: getDefaultPrice(seat.seatType)
        }

        // Verify the total matches (allow small difference for rounding)
        if ((calculatedTotal - request.totalPrice).abs() > BigDecimal(1000)) {
            throw BadRequestException("Total price mismatch. Expected: $calculatedTotal, Got: ${request.totalPrice}")
        }

        // 6. Generate booking code
        val bookingCode = generateBookingCode()

        // 7. Create booking
        val booking = Booking(
            bookingCode = bookingCode,
            user = user,
            showtime = showtime,
            status = BookingStatus.PENDING,
            totalAmount = calculatedTotal,
            expiresAt = OffsetDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES)
        )

        val savedBooking = bookingRepository.save(booking)

        // 8. Create booking items
        val bookingItems = seats.map { seat ->
            BookingItem(
                booking = savedBooking,
                seat = seat,
                price = priceMap[seat.seatType] ?: getDefaultPrice(seat.seatType)
            )
        }
        bookingItemRepository.saveAll(bookingItems)

        // 9. Remove user's seat locks (they've now booked)
        seatLockRepository.deleteUserLocksForShowtime(request.showtimeId, userId)

        // 10. Update available seats count
        updateAvailableSeats(request.showtimeId)

        // 11. Build response
        return buildBookingResponse(savedBooking, seats)
    }

    /**
     * Get booking by ID
     */
    @Transactional(readOnly = true)
    fun getBookingById(bookingId: Long): BookingResponseDto {
        val booking = bookingRepository.findByIdWithDetails(bookingId)
            ?: throw ResourceNotFoundException("Booking not found with id: $bookingId")

        val bookingItems = bookingItemRepository.findByBookingId(bookingId)
        val seats = bookingItems.map { it.seat }

        return buildBookingResponse(booking, seats)
    }

    /**
     * Get booking by code
     */
    @Transactional(readOnly = true)
    fun getBookingByCode(bookingCode: String): BookingResponseDto {
        val booking = bookingRepository.findByBookingCode(bookingCode)
            ?: throw ResourceNotFoundException("Booking not found with code: $bookingCode")

        val fullBooking = bookingRepository.findByIdWithDetails(booking.id)!!
        val bookingItems = bookingItemRepository.findByBookingId(booking.id)
        val seats = bookingItems.map { it.seat }

        return buildBookingResponse(fullBooking, seats)
    }

    /**
     * Get user's bookings
     */
    @Transactional(readOnly = true)
    fun getUserBookings(userId: Long): List<BookingResponseDto> {
        val bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)

        return bookings.mapNotNull { booking ->
            val fullBooking = bookingRepository.findByIdWithDetails(booking.id) ?: return@mapNotNull null
            val bookingItems = bookingItemRepository.findByBookingId(booking.id)
            val seats = bookingItems.map { it.seat }
            buildBookingResponse(fullBooking, seats)
        }
    }

    /**
     * Cancel booking
     */
    @Transactional
    fun cancelBooking(bookingId: Long, userId: Long): BookingResponseDto {
        val booking = bookingRepository.findByIdWithDetails(bookingId)
            ?: throw ResourceNotFoundException("Booking not found with id: $bookingId")

        if (booking.user.id != userId) {
            throw BadRequestException("You can only cancel your own bookings")
        }

        if (booking.status != BookingStatus.PENDING) {
            throw BadRequestException("Only pending bookings can be cancelled")
        }

        // Update booking status
        val updatedBooking = booking.copy(
            status = BookingStatus.CANCELLED,
            updatedAt = OffsetDateTime.now()
        )
        bookingRepository.save(updatedBooking)

        // Update available seats
        updateAvailableSeats(booking.showtime.id)

        val bookingItems = bookingItemRepository.findByBookingId(bookingId)
        val seats = bookingItems.map { it.seat }

        return buildBookingResponse(updatedBooking, seats)
    }

    // ===== Lock seats for reservation =====

    /**
     * Lock seats temporarily while user is selecting
     */
    @Transactional
    fun lockSeats(userId: Long, showtimeId: Long, seatIds: List<Long>): Boolean {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found") }

        val showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow { ResourceNotFoundException("Showtime not found") }

        // Check if seats are available
        val bookedSeatIds = bookingItemRepository.findBookedSeatIdsByShowtimeId(showtimeId).toSet()
        val lockedByOthers = seatLockRepository.findLocksForSeats(showtimeId, seatIds)
            .filter { it.user.id != userId }

        if (seatIds.any { bookedSeatIds.contains(it) } || lockedByOthers.isNotEmpty()) {
            return false
        }

        // Remove existing locks by this user for this showtime
        seatLockRepository.deleteUserLocksForShowtime(showtimeId, userId)

        // Create new locks
        val seats = seatRepository.findAllById(seatIds)
        val lockExpiry = OffsetDateTime.now().plusMinutes(10)

        val locks = seats.map { seat ->
            SeatLock(
                showtime = showtime,
                seat = seat,
                user = user,
                lockedUntil = lockExpiry
            )
        }
        seatLockRepository.saveAll(locks)

        return true
    }

    /**
     * Unlock seats
     */
    @Transactional
    fun unlockSeats(userId: Long, showtimeId: Long) {
        seatLockRepository.deleteUserLocksForShowtime(showtimeId, userId)
    }

    // ===== Private helper methods =====

    private fun generateBookingCode(): String {
        val date = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val random = UUID.randomUUID().toString().substring(0, 6).uppercase()
        return "BK$date$random"
    }

    private fun getDefaultPrice(seatType: SeatType): BigDecimal {
        return when (seatType) {
            SeatType.STANDARD -> BigDecimal(85000)
            SeatType.VIP -> BigDecimal(110000)
            SeatType.COUPLE -> BigDecimal(200000)
            SeatType.DELUXE -> BigDecimal(150000)
        }
    }

    private fun updateAvailableSeats(showtimeId: Long) {
        val showtime = showtimeRepository.findById(showtimeId).orElse(null) ?: return
        val bookedCount = bookingItemRepository.findBookedSeatIdsByShowtimeId(showtimeId).size
        val totalSeats = showtime.screen.totalSeats.toInt()
        val available = (totalSeats - bookedCount).coerceAtLeast(0).toShort()

        val updatedShowtime = showtime.copy(
            availableSeats = available,
            updatedAt = OffsetDateTime.now()
        )
        showtimeRepository.save(updatedShowtime)
    }

    private fun buildBookingResponse(booking: Booking, seats: List<Seat>): BookingResponseDto {
        val showtime = booking.showtime
        val movie = showtime.movie
        val cinema = showtime.screen.cinema

        return BookingResponseDto(
            id = booking.id,
            bookingCode = booking.bookingCode,
            status = booking.status.name,
            movie = BookingMovieDto(
                id = movie.id,
                title = movie.title
            ),
            cinema = BookingCinemaDto(
                id = cinema.id,
                name = cinema.name
            ),
            showtime = BookingShowtimeDto(
                id = showtime.id,
                date = showtime.startTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                time = showtime.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                screenName = showtime.screen.name
            ),
            seats = seats.map { "${it.rowName}${it.seatNumber}" },
            totalPrice = booking.totalAmount,
            expiresAt = booking.expiresAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            createdAt = booking.createdAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}
