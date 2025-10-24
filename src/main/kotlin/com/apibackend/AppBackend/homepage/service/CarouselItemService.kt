package com.apibackend.AppBackend.movies.service

import com.apibackend.AppBackend.movies.dto.CarouselItemDto
import com.apibackend.AppBackend.movies.mapper.CarouselItemMapper
import com.apibackend.AppBackend.movies.repository.CarouselItemRepository
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
