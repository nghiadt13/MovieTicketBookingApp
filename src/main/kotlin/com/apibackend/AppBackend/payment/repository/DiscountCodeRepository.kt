package com.apibackend.AppBackend.payment.repository

import com.apibackend.AppBackend.payment.model.DiscountCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface DiscountCodeRepository : JpaRepository<DiscountCode, Long> {

    fun findByCode(code: String): DiscountCode?

    @Query("""
        SELECT d FROM DiscountCode d
        WHERE d.code = :code
        AND d.isActive = true
        AND d.validFrom <= :now
        AND d.validUntil >= :now
        AND (d.usageLimit IS NULL OR d.usedCount < d.usageLimit)
    """)
    fun findValidDiscountCode(
        @Param("code") code: String,
        @Param("now") now: OffsetDateTime = OffsetDateTime.now()
    ): DiscountCode?
}
