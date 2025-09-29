package com.apibackend.AppBackend.mapper

import com.apibackend.AppBackend.dto.*
import com.apibackend.AppBackend.model.Format
import com.apibackend.AppBackend.model.Genre
import com.apibackend.AppBackend.model.Movie
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface MovieMapper {

    companion object {
        val INSTANCE: MovieMapper = Mappers.getMapper(MovieMapper::class.java)
    }

    fun movieToDto(movie: Movie): MovieDto

    fun genreToDto(genre: Genre): GenreDto

    fun formatToDto(format: Format): FormatDto

    fun moviesToDtos(movies: List<Movie>): List<MovieDto>

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingAvg", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "formats", ignore = true)
    fun createDtoToMovie(createDto: CreateMovieDto): Movie

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingAvg", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "formats", ignore = true)
    fun updateMovieFromDto(updateDto: UpdateMovieDto, @MappingTarget movie: Movie): Movie
}
