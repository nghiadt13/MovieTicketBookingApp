package com.apibackend.AppBackend.homepage.controller

import com.apibackend.AppBackend.homepage.dto.MembershipTierDto
import com.apibackend.AppBackend.homepage.service.MembershipTierService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/membership-tiers")
@Tag(name = "Membership Tiers", description = "Membership tier management APIs")
class MembershipTierController(private val membershipTierService: MembershipTierService) {

    @GetMapping
    @Operation(
            summary = "Get all membership tiers",
            description = "Returns all membership tiers ordered by rank"
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successful retrieval")])
    fun getAllMembershipTiers(): ResponseEntity<List<MembershipTierDto>> {
        val tiers = membershipTierService.getAllMembershipTiers()
        return ResponseEntity.ok(tiers)
    }
}
