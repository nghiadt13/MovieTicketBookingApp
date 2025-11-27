package com.apibackend.AppBackend.payment.repository

import com.apibackend.AppBackend.payment.model.Payment
import com.apibackend.AppBackend.payment.model.PaymentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository : JpaRepository<Payment, Long> {

    fun findByTransactionId(transactionId: String): Payment?

    fun findByBookingId(bookingId: Long): List<Payment>

    fun findByBookingIdAndStatus(bookingId: Long, status: PaymentStatus): Payment?

    @Query("""
        SELECT p FROM Payment p
        JOIN FETCH p.booking b
        JOIN FETCH p.paymentMethod
        WHERE p.id = :id
    """)
    fun findByIdWithDetails(@Param("id") id: Long): Payment?

    @Query("""
        SELECT p FROM Payment p
        WHERE p.booking.id = :bookingId
        AND p.status IN ('PENDING', 'PROCESSING')
    """)
    fun findPendingPaymentByBookingId(@Param("bookingId") bookingId: Long): Payment?

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Payment p
        WHERE p.booking.id = :bookingId
        AND p.status = 'COMPLETED'
    """)
    fun hasCompletedPayment(@Param("bookingId") bookingId: Long): Boolean
}
