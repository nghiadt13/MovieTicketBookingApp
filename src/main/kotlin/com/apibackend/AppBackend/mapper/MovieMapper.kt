package com.apibackend.AppBackend.mapper

import com.apibackend.AppBackend.dto.*
import com.apibackend.AppBackend.model.Format
import com.apibackend.AppBackend.model.Genre
import com.apibackend.AppBackend.model.Movie
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface MovieMapper {

    // Spring will provide this mapper as a bean (componentModel = "spring")

    fun movieToDto(movie: Movie): MovieDto

    fun genreToDto(genre: Genre): GenreDto

    fun formatToDto(format: Format): FormatDto

    fun moviesToDtos(movies: List<Movie>): List<MovieDto>

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingAvg", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "ratingCount", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "genres", expression = "java(new java.util.HashSet<com.apibackend.AppBackend.model.Genre>())")
    @Mapping(target = "formats", expression = "java(new java.util.HashSet<com.apibackend.AppBackend.model.Format>())")
    fun createDtoToMovie(createDto: CreateMovieDto): Movie
}
