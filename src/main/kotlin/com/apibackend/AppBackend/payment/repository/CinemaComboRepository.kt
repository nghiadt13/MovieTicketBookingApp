package com.apibackend.AppBackend.payment.repository

import com.apibackend.AppBackend.payment.model.CinemaCombo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CinemaComboRepository : JpaRepository<CinemaCombo, Long> {

    fun findByCinemaIdAndIsAvailableTrue(cinemaId: Long): List<CinemaCombo>
}
