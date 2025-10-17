package com.apibackend.AppBackend.service

import com.apibackend.AppBackend.dto.CarouselItemDto
import com.apibackend.AppBackend.mapper.CarouselItemMapper
import com.apibackend.AppBackend.repository.CarouselItemRepository
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
