package com.apibackend.AppBackend.payment.service

import com.apibackend.AppBackend.booking.model.Booking
import com.apibackend.AppBackend.booking.model.BookingStatus
import com.apibackend.AppBackend.booking.repository.BookingItemRepository
import com.apibackend.AppBackend.booking.repository.BookingRepository
import com.apibackend.AppBackend.payment.dto.*
import com.apibackend.AppBackend.payment.exception.*
import com.apibackend.AppBackend.payment.mapper.ComboMapper
import com.apibackend.AppBackend.payment.mapper.PaymentMethodMapper
import com.apibackend.AppBackend.payment.model.*
import com.apibackend.AppBackend.payment.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class PaymentService(
    private val bookingRepository: BookingRepository,
    private val bookingItemRepository: BookingItemRepository,
    private val comboRepository: ComboRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val paymentRepository: PaymentRepository,
    private val discountCodeRepository: DiscountCodeRepository,
    private val comboMapper: ComboMapper,
    private val paymentMethodMapper: PaymentMethodMapper
) {

    /**
     * Get booking info for payment screen
     */
    @Transactional(readOnly = true)
    fun getBookingInfoForPayment(bookingId: Long): BookingInfoDto {
        val booking = bookingRepository.findByIdWithFullDetails(bookingId)
            ?: throw BookingNotFoundException(bookingId)

        validateBookingForPayment(booking)

        val bookingItems = bookingItemRepository.findByBookingId(bookingId)
        val seats = bookingItems.map { "${it.seat.rowName}${it.seat.seatNumber}" }

        val showtime = booking.showtime
        val movie = showtime.movie
        val cinema = showtime.screen.cinema

        // Get first genre as main genre
        val genre = movie.genres.firstOrNull()?.name ?: "Unknown"

        // Get format from showtime's format or fallback to movie's formats
        val format = showtime.format?.label
            ?: movie.formats.firstOrNull()?.label
            ?: "2D"

        // Calculate ticket price per seat
        val ticketPrice = if (bookingItems.isNotEmpty()) {
            bookingItems.first().price.toLong()
        } else {
            booking.totalAmount.divide(BigDecimal(bookingItems.size.coerceAtLeast(1))).toLong()
        }

        return BookingInfoDto(
            movieTitle = movie.title,
            moviePosterUrl = movie.posterUrl,
            genre = genre,
            format = format,
            duration = "${movie.durationMin} min",
            cinemaName = cinema.name,
            showtime = showtime.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            showdate = formatShowDate(showtime.startTime),
            seats = seats,
            room = showtime.screen.name,
            ticketPrice = ticketPrice,
            ticketCount = bookingItems.size
        )
    }

    /**
     * Get all active combos
     */
    @Transactional(readOnly = true)
    fun getAllActiveCombos(): List<ComboDto> {
        val combos = comboRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
        return comboMapper.combosToDtos(combos)
    }

    /**
     * Get combos by cinema
     */
    @Transactional(readOnly = true)
    fun getCombosByCinema(cinemaId: Long): List<ComboDto> {
        val combos = comboRepository.findActiveByCinemaId(cinemaId)
        return if (combos.isEmpty()) {
            // Fallback to all active combos if cinema has no specific combos
            getAllActiveCombos()
        } else {
            comboMapper.combosToDtos(combos)
        }
    }

    /**
     * Get all active payment methods
     */
    @Transactional(readOnly = true)
    fun getAllActivePaymentMethods(): List<PaymentMethodDto> {
        val paymentMethods = paymentMethodRepository.findByIsActiveTrueOrderByDisplayOrderAsc()
        return paymentMethodMapper.paymentMethodsToDtos(paymentMethods)
    }

    /**
     * Calculate payment summary
     */
    @Transactional(readOnly = true)
    fun calculatePayment(request: CalculatePaymentRequest): PaymentSummaryDto {
        val booking = bookingRepository.findByIdWithDetails(request.bookingId)
            ?: throw BookingNotFoundException(request.bookingId)

        validateBookingForPayment(booking)

        val bookingItems = bookingItemRepository.findByBookingId(request.bookingId)
        val ticketCount = bookingItems.size
        val ticketPrice = booking.totalAmount.toLong()

        // Calculate combo price
        val comboPrice = calculateComboTotal(request.combos)

        // Calculate discount
        val subtotal = ticketPrice + comboPrice
        val discount = calculateDiscount(request.discountCode, subtotal)

        val totalPrice = subtotal - discount

        return PaymentSummaryDto(
            ticketPrice = ticketPrice,
            ticketCount = ticketCount,
            comboPrice = comboPrice,
            discount = discount,
            totalPrice = totalPrice.coerceAtLeast(0)
        )
    }

    /**
     * Create a payment
     */
    @Transactional
    fun createPayment(request: CreatePaymentRequest): PaymentResponseDto {
        // 1. Validate booking
        val booking = bookingRepository.findByIdWithDetails(request.bookingId)
            ?: throw BookingNotFoundException(request.bookingId)

        validateBookingForPayment(booking)

        // 2. Check if booking already has a pending/completed payment
        val existingPayment = paymentRepository.findPendingPaymentByBookingId(request.bookingId)
        if (existingPayment != null) {
            return PaymentResponseDto(
                success = true,
                transactionId = existingPayment.transactionId,
                qrCodeUrl = existingPayment.qrCodeUrl,
                message = "Payment already exists"
            )
        }

        if (paymentRepository.hasCompletedPayment(request.bookingId)) {
            throw BookingAlreadyPaidException(request.bookingId)
        }

        // 3. Validate payment method
        val paymentMethod = paymentMethodRepository.findById(request.paymentMethodId)
            .orElseThrow { PaymentMethodNotFoundException(request.paymentMethodId) }

        if (!paymentMethod.isActive) {
            throw PaymentMethodInvalidException(request.paymentMethodId)
        }

        // 4. Validate combos
        val comboEntities = validateAndGetCombos(request.combos)

        // 5. Calculate prices
        val bookingItems = bookingItemRepository.findByBookingId(request.bookingId)
        val ticketCount = bookingItems.size
        val ticketPrice = booking.totalAmount

        val comboPrice = calculateComboPriceFromEntities(request.combos, comboEntities)
        val subtotal = ticketPrice.toLong() + comboPrice

        // 6. Validate and apply discount
        val discountCode = request.discountCode?.let { code ->
            val discount = discountCodeRepository.findValidDiscountCode(code)
                ?: throw InvalidDiscountCodeException(code)

            if (subtotal < discount.minOrderAmount.toLong()) {
                throw MinOrderNotMetException(code, discount.minOrderAmount.toLong())
            }

            discount
        }

        val discountAmount = discountCode?.let { calculateDiscountAmount(it, subtotal) } ?: 0L
        val totalAmount = (subtotal - discountAmount).coerceAtLeast(0)

        // 7. Generate transaction ID and QR code URL
        val transactionId = generateTransactionId()
        val qrCodeUrl = generateQrCodeUrl(paymentMethod, transactionId, totalAmount)

        // 8. Create payment record
        val payment = Payment(
            transactionId = transactionId,
            booking = booking,
            paymentMethod = paymentMethod,
            ticketPrice = ticketPrice,
            ticketCount = ticketCount,
            comboPrice = BigDecimal(comboPrice),
            discountAmount = BigDecimal(discountAmount),
            totalAmount = BigDecimal(totalAmount),
            discountCode = discountCode,
            qrCodeUrl = qrCodeUrl,
            status = PaymentStatus.PENDING
        )

        val savedPayment = paymentRepository.save(payment)

        // 9. Create payment combos
        request.combos.forEach { comboSelection ->
            val combo = comboEntities[comboSelection.comboId]!!
            val paymentCombo = PaymentCombo(
                payment = savedPayment,
                combo = combo,
                quantity = comboSelection.quantity,
                unitPrice = combo.price,
                subtotal = combo.price.multiply(BigDecimal(comboSelection.quantity))
            )
            savedPayment.combos.add(paymentCombo)
        }

        // 10. Update discount code usage count
        discountCode?.let {
            val updatedDiscount = it.copy(usedCount = it.usedCount + 1)
            discountCodeRepository.save(updatedDiscount)
        }

        return PaymentResponseDto(
            success = true,
            transactionId = transactionId,
            qrCodeUrl = qrCodeUrl,
            message = "Payment created successfully"
        )
    }

    /**
     * Complete a payment (to be called by payment gateway webhook)
     */
    @Transactional
    fun completePayment(transactionId: String, gatewayTransactionId: String): PaymentResponseDto {
        val payment = paymentRepository.findByTransactionId(transactionId)
            ?: throw PaymentNotFoundException(transactionId)

        if (payment.status == PaymentStatus.COMPLETED) {
            return PaymentResponseDto(
                success = true,
                transactionId = transactionId,
                message = "Payment already completed"
            )
        }

        // Update payment status
        val completedPayment = payment.copy(
            status = PaymentStatus.COMPLETED,
            gatewayTransactionId = gatewayTransactionId,
            paidAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
        paymentRepository.save(completedPayment)

        // Update booking status
        val booking = payment.booking
        val confirmedBooking = booking.copy(
            status = BookingStatus.CONFIRMED,
            paymentMethod = payment.paymentMethod.id,
            paymentTransactionId = transactionId,
            paidAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now()
        )
        bookingRepository.save(confirmedBooking)

        return PaymentResponseDto(
            success = true,
            transactionId = transactionId,
            message = "Payment completed successfully"
        )
    }

    // ===== Private helper methods =====

    private fun validateBookingForPayment(booking: Booking) {
        if (booking.status != BookingStatus.PENDING) {
            if (booking.status == BookingStatus.CONFIRMED) {
                throw BookingAlreadyPaidException(booking.id)
            }
            throw BookingExpiredException(booking.id)
        }

        if (booking.expiresAt.isBefore(OffsetDateTime.now())) {
            throw BookingExpiredException(booking.id)
        }
    }

    private fun formatShowDate(dateTime: OffsetDateTime): String {
        val today = OffsetDateTime.now().toLocalDate()
        val showDate = dateTime.toLocalDate()

        return when {
            showDate == today -> "Today, ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM"))}"
            showDate == today.plusDays(1) -> "Tomorrow, ${dateTime.format(DateTimeFormatter.ofPattern("dd/MM"))}"
            else -> dateTime.format(DateTimeFormatter.ofPattern("EEE, dd/MM"))
        }
    }

    private fun calculateComboTotal(combos: List<ComboSelectionDto>): Long {
        if (combos.isEmpty()) return 0L

        val comboIds = combos.map { it.comboId }
        val comboEntities = comboRepository.findActiveByIds(comboIds).associateBy { it.id }

        return combos.sumOf { selection ->
            val combo = comboEntities[selection.comboId]
                ?: throw ComboNotFoundException(selection.comboId)
            combo.price.toLong() * selection.quantity
        }
    }

    private fun calculateComboPriceFromEntities(
        selections: List<ComboSelectionDto>,
        comboEntities: Map<Long, Combo>
    ): Long {
        return selections.sumOf { selection ->
            val combo = comboEntities[selection.comboId]!!
            combo.price.toLong() * selection.quantity
        }
    }

    private fun validateAndGetCombos(combos: List<ComboSelectionDto>): Map<Long, Combo> {
        if (combos.isEmpty()) return emptyMap()

        val comboIds = combos.map { it.comboId }
        val comboEntities = comboRepository.findActiveByIds(comboIds).associateBy { it.id }

        combos.forEach { selection ->
            if (!comboEntities.containsKey(selection.comboId)) {
                throw ComboNotFoundException(selection.comboId)
            }
        }

        return comboEntities
    }

    private fun calculateDiscount(discountCode: String?, subtotal: Long): Long {
        if (discountCode.isNullOrBlank()) return 0L

        val discount = discountCodeRepository.findValidDiscountCode(discountCode)
            ?: return 0L

        if (subtotal < discount.minOrderAmount.toLong()) return 0L

        return calculateDiscountAmount(discount, subtotal)
    }

    private fun calculateDiscountAmount(discount: DiscountCode, subtotal: Long): Long {
        val discountAmount = when (discount.discountType) {
            DiscountType.PERCENTAGE -> (subtotal * discount.discountValue.toLong()) / 100
            DiscountType.FIXED_AMOUNT -> discount.discountValue.toLong()
        }

        return discount.maxDiscountAmount?.let { max ->
            discountAmount.coerceAtMost(max.toLong())
        } ?: discountAmount
    }

    private fun generateTransactionId(): String {
        val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = UUID.randomUUID().toString().substring(0, 8).uppercase()
        return "TXN$timestamp$random"
    }

    private fun generateQrCodeUrl(paymentMethod: PaymentMethod, transactionId: String, amount: Long): String? {
        // In production, this would integrate with actual payment gateway
        // For now, return a placeholder URL based on payment method
        return when (paymentMethod.iconType) {
            PaymentIconType.VIETQR -> "https://api.vietqr.io/image/970415-${transactionId}-${amount}.jpg"
            PaymentIconType.MOMO -> "https://momo.vn/payment/${transactionId}"
            PaymentIconType.ZALOPAY -> "https://zalopay.vn/payment/${transactionId}"
            else -> null
        }
    }
}
