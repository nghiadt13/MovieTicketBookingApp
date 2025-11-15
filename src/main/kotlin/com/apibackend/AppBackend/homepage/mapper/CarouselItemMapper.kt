package com.apibackend.AppBackend.homepage.mapper

import com.apibackend.AppBackend.homepage.dto.CarouselItemDto
import com.apibackend.AppBackend.homepage.model.CarouselItem
import org.springframework.stereotype.Component

@Component
class CarouselItemMapper {

    fun toDto(carouselItem: CarouselItem): CarouselItemDto {
        return CarouselItemDto(
                id = carouselItem.id!!,
                title = carouselItem.title,
                imageUrl = carouselItem.imageUrl,
                content = carouselItem.content,
                targetUrl = carouselItem.targetUrl,
                isActive = carouselItem.isActive,
                createdAt = carouselItem.createdAt,
                updatedAt = carouselItem.updatedAt
        )
    }
}
