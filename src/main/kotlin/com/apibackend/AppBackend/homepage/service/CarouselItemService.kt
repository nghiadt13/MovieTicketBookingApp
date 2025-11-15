package com.apibackend.AppBackend.homepage.service

import com.apibackend.AppBackend.homepage.dto.CarouselItemDto
import com.apibackend.AppBackend.homepage.mapper.CarouselItemMapper
import com.apibackend.AppBackend.homepage.repository.CarouselItemRepository
import org.springframework.stereotype.Service

@Service
class CarouselItemService(
        private val carouselItemRepository: CarouselItemRepository,
        private val carouselItemMapper: CarouselItemMapper
) {

    fun getLatestActiveCarouselItems(): List<CarouselItemDto> {
        return carouselItemRepository.findTop5ActiveCarouselItems().take(5).map {
            carouselItemMapper.toDto(it)
        }
    }
}
