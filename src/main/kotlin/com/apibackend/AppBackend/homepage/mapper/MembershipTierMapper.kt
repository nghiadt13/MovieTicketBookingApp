package com.apibackend.AppBackend.homepage.mapper

import com.apibackend.AppBackend.homepage.dto.MembershipTierDto
import com.apibackend.AppBackend.homepage.model.MembershipTier
import org.springframework.stereotype.Component

@Component
class MembershipTierMapper {

    fun toDto(membershipTier: MembershipTier): MembershipTierDto {
        return MembershipTierDto(
                id = membershipTier.id,
                name = membershipTier.name,
                imageUrl = membershipTier.imageUrl,
                description = membershipTier.description
        )
    }
}
