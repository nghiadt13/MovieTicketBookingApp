package com.apibackend.AppBackend.movies.mapper

import com.apibackend.AppBackend.movies.dto.*
import com.apibackend.AppBackend.movies.model.Format
import com.apibackend.AppBackend.movies.model.Genre
import com.apibackend.AppBackend.movies.model.Movie
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
                    "java(new java.util.HashSet<com.apibackend.AppBackend.movies.model.Genre>())"
    )
    @Mapping(
            target = "formats",
            expression =
                    "java(new java.util.HashSet<com.apibackend.AppBackend.movies.model.Format>())"
    )
    fun createDtoToMovie(createDto: CreateMovieDto): Movie
}
