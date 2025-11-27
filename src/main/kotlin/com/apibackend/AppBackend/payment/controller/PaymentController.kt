package com.apibackend.AppBackend.payment.controller

import com.apibackend.AppBackend.common.config.ApiError
import com.apibackend.AppBackend.payment.dto.*
import com.apibackend.AppBackend.payment.service.PaymentService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payments", description = "Payment management endpoints")
class PaymentController(
    private val paymentService: PaymentService
) {

    // ===== Booking Info Endpoint =====

    @GetMapping("/bookings/{bookingId}")
    @Operation(
        summary = "Get booking info for payment",
        description = """
            Returns booking information needed for the payment screen.
            Includes movie details, showtime, seats, and ticket price.

            This endpoint validates that the booking is in PENDING status and not expired.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Booking info retrieved successfully",
                content = [Content(schema = Schema(implementation = BookingInfoResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Booking has expired or already paid",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun getBookingInfoForPayment(
        @Parameter(description = "Booking ID", required = true)
        @PathVariable bookingId: Long
    ): ResponseEntity<BookingInfoResponse> {
        val bookingInfo = paymentService.getBookingInfoForPayment(bookingId)
        return ResponseEntity.ok(BookingInfoResponse(bookingInfo))
    }

    // ===== Combos Endpoints =====

    @GetMapping("/combos")
    @Operation(
        summary = "Get all available combos",
        description = "Returns all active combo items (popcorn, drinks, etc.) available for purchase"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Combos retrieved successfully",
                content = [Content(schema = Schema(implementation = CombosResponse::class))]
            )
        ]
    )
    fun getAllCombos(): ResponseEntity<CombosResponse> {
        val combos = paymentService.getAllActiveCombos()
        return ResponseEntity.ok(CombosResponse(combos))
    }

    @GetMapping("/cinemas/{cinemaId}/combos")
    @Operation(
        summary = "Get combos by cinema",
        description = "Returns combo items available at a specific cinema. Falls back to all combos if cinema has no specific combos."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Combos retrieved successfully",
                content = [Content(schema = Schema(implementation = CombosResponse::class))]
            )
        ]
    )
    fun getCombosByCinema(
        @Parameter(description = "Cinema ID", required = true)
        @PathVariable cinemaId: Long
    ): ResponseEntity<CombosResponse> {
        val combos = paymentService.getCombosByCinema(cinemaId)
        return ResponseEntity.ok(CombosResponse(combos))
    }

    // ===== Payment Methods Endpoint =====

    @GetMapping("/payment-methods")
    @Operation(
        summary = "Get available payment methods",
        description = "Returns all active payment methods (VietQR, MoMo, ZaloPay, etc.)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Payment methods retrieved successfully",
                content = [Content(schema = Schema(implementation = PaymentMethodsResponse::class))]
            )
        ]
    )
    fun getPaymentMethods(): ResponseEntity<PaymentMethodsResponse> {
        val paymentMethods = paymentService.getAllActivePaymentMethods()
        return ResponseEntity.ok(PaymentMethodsResponse(paymentMethods))
    }

    // ===== Calculate Payment Endpoint =====

    @PostMapping("/payments/calculate")
    @Operation(
        summary = "Calculate payment total",
        description = """
            Calculates the total payment amount including:
            - Ticket price
            - Combo price
            - Discount (if valid discount code provided)

            Use this endpoint when user changes combo selection or applies discount code.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Payment calculated successfully",
                content = [Content(schema = Schema(implementation = PaymentSummaryDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request (e.g., booking expired, invalid combo)",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking or combo not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun calculatePayment(
        @Valid @RequestBody request: CalculatePaymentRequest
    ): ResponseEntity<PaymentSummaryDto> {
        val summary = paymentService.calculatePayment(request)
        return ResponseEntity.ok(summary)
    }

    // ===== Create Payment Endpoint =====

    @PostMapping("/payments")
    @Operation(
        summary = "Create a payment",
        description = """
            Creates a new payment transaction for a booking.

            Returns:
            - Transaction ID
            - QR code URL (for VietQR/MoMo/ZaloPay)

            The payment will be in PENDING status until confirmed by the payment gateway.

            Note: Combos array can be empty if no combos are selected.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Payment created successfully",
                content = [Content(schema = Schema(implementation = PaymentResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request (e.g., booking expired, invalid payment method, invalid combo)",
                content = [Content(schema = Schema(implementation = PaymentErrorDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Booking, payment method, or combo not found",
                content = [Content(schema = Schema(implementation = PaymentErrorDto::class))]
            )
        ]
    )
    fun createPayment(
        @Valid @RequestBody request: CreatePaymentRequest
    ): ResponseEntity<PaymentResponseDto> {
        val response = paymentService.createPayment(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // ===== Payment Webhook Endpoint (for payment gateway callbacks) =====

    @PostMapping("/payments/{transactionId}/complete")
    @Operation(
        summary = "Complete a payment (webhook)",
        description = """
            Webhook endpoint for payment gateway to confirm payment completion.

            This endpoint should be called by the payment gateway after successful payment.
            It will:
            - Update payment status to COMPLETED
            - Update booking status to CONFIRMED
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Payment completed successfully",
                content = [Content(schema = Schema(implementation = PaymentResponseDto::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Payment not found",
                content = [Content(schema = Schema(implementation = ApiError::class))]
            )
        ]
    )
    fun completePayment(
        @Parameter(description = "Transaction ID", required = true)
        @PathVariable transactionId: String,
        @Parameter(description = "Gateway transaction ID")
        @RequestParam(required = false) gatewayTransactionId: String?
    ): ResponseEntity<PaymentResponseDto> {
        val response = paymentService.completePayment(transactionId, gatewayTransactionId ?: "")
        return ResponseEntity.ok(response)
    }
}
