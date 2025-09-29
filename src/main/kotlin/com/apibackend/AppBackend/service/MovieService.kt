package com.apibackend.AppBackend.service

import com.apibackend.AppBackend.dto.FormatDto
import com.apibackend.AppBackend.dto.GenreDto
import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.model.Format
import com.apibackend.AppBackend.model.Genre
import com.apibackend.AppBackend.model.Movie
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.repository.MovieRepository
import org.springframework.stereotype.Service

@Service
class MovieService(private val movieRepository: MovieRepository) {

    fun getAllActiveMovies(): List<MovieDto> {
        return movieRepository.findAllActiveWithGenresAndFormats().map { it.toDto() }
    }

    fun getMoviesByStatus(status: MovieStatus): List<MovieDto> {
        return movieRepository.findByStatusWithGenresAndFormats(status).map { it.toDto() }
    }

    private fun Movie.toDto(): MovieDto {
        return MovieDto(
                id = this.id,
                title = this.title,
                synopsis = this.synopsis,
                durationMin = this.durationMin,
                releaseDate = this.releaseDate,
                status = this.status,
                posterUrl = this.posterUrl,
                trailerUrl = this.trailerUrl,  
                ratingAvg = this.ratingAvg,
                ratingCount = this.ratingCount,
                genres = this.genres.map { it.toDto() },
                formats = this.formats.map { it.toDto() }
        )
    }

    private fun Genre.toDto(): GenreDto {
        return GenreDto(id = this.id, name = this.name, slug = this.slug)
    } 

    private fun Format.toDto(): FormatDto {
        return FormatDto(id = this.id, code = this.code, label = this.label)
    }
}
 