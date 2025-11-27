package com.apibackend.AppBackend.payment.repository

import com.apibackend.AppBackend.payment.model.Combo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ComboRepository : JpaRepository<Combo, Long> {

    fun findByIsActiveTrueOrderByDisplayOrderAsc(): List<Combo>

    @Query("""
        SELECT c FROM Combo c
        WHERE c.isActive = true
        AND c.id IN :ids
    """)
    fun findActiveByIds(@Param("ids") ids: List<Long>): List<Combo>

    @Query("""
        SELECT c FROM Combo c
        JOIN CinemaCombo cc ON cc.combo = c
        WHERE cc.cinema.id = :cinemaId
        AND cc.isAvailable = true
        AND c.isActive = true
        ORDER BY c.displayOrder ASC
    """)
    fun findActiveByCinemaId(@Param("cinemaId") cinemaId: Long): List<Combo>
}
