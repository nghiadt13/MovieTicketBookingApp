package com.apibackend.AppBackend.homepage.repository

import com.apibackend.AppBackend.homepage.model.MembershipTier
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MembershipTierRepository : JpaRepository<MembershipTier, Long> {
    fun findAllByOrderByRankOrderAsc(): List<MembershipTier>
}
