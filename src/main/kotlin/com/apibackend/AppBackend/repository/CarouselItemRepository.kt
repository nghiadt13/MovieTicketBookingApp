package com.apibackend.AppBackend.repository

import com.apibackend.AppBackend.model.CarouselItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface CarouselItemRepository : JpaRepository<CarouselItem, Long> {

    @Query("SELECT c FROM CarouselItem c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    fun findTop5ActiveCarouselItems(): List<CarouselItem>
}
