package com.apibackend.AppBackend.homepage.mapper

import com.apibackend.AppBackend.homepage.dto.*
import com.apibackend.AppBackend.homepage.model.Format
import com.apibackend.AppBackend.homepage.model.Genre
import com.apibackend.AppBackend.homepage.model.Movie
import org.mapstruct.BeanMapping
import org.mapstruct.Builder
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface MovieMapper {

    // Spring will provide this mapper as a bean (componentModel = "spring")

    fun movieToDto(movie: Movie): MovieDto

    fun genreToDto(genre: Genre): GenreDto

    fun formatToDto(format: Format): FormatDto

    fun moviesToDtos(movies: List<Movie>): List<MovieDto>

    @BeanMapping(builder = Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(
            target = "genres",
            expression =
                    "java(new java.util.HashSet<com.apibackend.AppBackend.homepage.model.Genre>())"
    )
    @Mapping(
            target = "formats",
            expression =
                    "java(new java.util.HashSet<com.apibackend.AppBackend.homepage.model.Format>())"
    )
    fun createDtoToMovie(createDto: CreateMovieDto): Movie
}
