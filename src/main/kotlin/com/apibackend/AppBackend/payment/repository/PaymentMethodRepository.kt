package com.apibackend.AppBackend.payment.repository

import com.apibackend.AppBackend.payment.model.PaymentMethod
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodRepository : JpaRepository<PaymentMethod, String> {

    fun findByIsActiveTrueOrderByDisplayOrderAsc(): List<PaymentMethod>
}
