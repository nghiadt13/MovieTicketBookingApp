package com.apibackend.AppBackend.homepage.service

import com.apibackend.AppBackend.homepage.dto.MembershipTierDto
import com.apibackend.AppBackend.homepage.mapper.MembershipTierMapper
import com.apibackend.AppBackend.homepage.repository.MembershipTierRepository
import org.springframework.stereotype.Service

@Service
class MembershipTierService(
        private val membershipTierRepository: MembershipTierRepository,
        private val membershipTierMapper: MembershipTierMapper
) {

    fun getAllMembershipTiers(): List<MembershipTierDto> {
        return membershipTierRepository.findAllByOrderByRankOrderAsc().map {
            membershipTierMapper.toDto(it)
        }
    }
}
