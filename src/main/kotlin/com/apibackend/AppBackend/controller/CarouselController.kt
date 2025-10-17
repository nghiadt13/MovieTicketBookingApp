package com.apibackend.AppBackend.controller

import com.apibackend.AppBackend.dto.CarouselItemDto
import com.apibackend.AppBackend.service.CarouselItemService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/carousel")
@Tag(name = "Carousel", description = "Carousel management APIs")
class CarouselController(private val carouselItemService: CarouselItemService) {

    @GetMapping("/latest")
    @Operation(
            summary = "Get latest 5 active carousel items",
            description = "Returns the 5 most recent active carousel items"
    )
    fun getLatestCarouselItems(): ResponseEntity<List<CarouselItemDto>> {
        val carouselItems = carouselItemService.getLatestActiveCarouselItems()
        return ResponseEntity.ok(carouselItems)
    }
}
