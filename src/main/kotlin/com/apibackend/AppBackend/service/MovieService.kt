package com.apibackend.AppBackend.service

import com.apibackend.AppBackend.dto.CreateMovieDto
import com.apibackend.AppBackend.dto.MovieDto
import com.apibackend.AppBackend.dto.UpdateMovieDto
import com.apibackend.AppBackend.mapper.MovieMapper
import com.apibackend.AppBackend.model.MovieStatus
import com.apibackend.AppBackend.repository.FormatRepository
import com.apibackend.AppBackend.repository.GenreRepository
import com.apibackend.AppBackend.repository.MovieRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.OffsetDateTime
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MovieService(
        private val movieRepository: MovieRepository,
        private val genreRepository: GenreRepository,
        private val formatRepository: FormatRepository,
        private val movieMapper: MovieMapper
) {

    fun getAllActiveMovies(): List<MovieDto> {
        return movieMapper.moviesToDtos(movieRepository.findAllActiveWithGenresAndFormats())
    }

    fun getMoviesByStatus(status: MovieStatus): List<MovieDto> {
        return movieMapper.moviesToDtos(movieRepository.findByStatusWithGenresAndFormats(status))
    }

    fun getMovieById(id: Long): MovieDto? {
        return movieRepository.findByIdWithGenresAndFormats(id)?.let { movieMapper.movieToDto(it) }
    }

    fun createMovie(createDto: CreateMovieDto): MovieDto {
        val genres = genreRepository.findAllById(createDto.genreIds).toSet()
        val formats = formatRepository.findAllById(createDto.formatIds).toSet()

        val movie =
                movieMapper
                        .createDtoToMovie(createDto)
                        .copy(
                                genres = genres,
                                formats = formats,
                                createdAt = OffsetDateTime.now(),
                                updatedAt = OffsetDateTime.now()
                        )

        val savedMovie = movieRepository.save(movie)
        return movieMapper.movieToDto(savedMovie)
    }

    fun updateMovie(id: Long, updateDto: UpdateMovieDto): MovieDto? {
        val existingMovie = movieRepository.findByIdOrNull(id) ?: return null

        val genres =
                updateDto.genreIds?.let { genreRepository.findAllById(it).toSet() }
                        ?: existingMovie.genres
        val formats =
                updateDto.formatIds?.let { formatRepository.findAllById(it).toSet() }
                        ?: existingMovie.formats

        val updatedMovie =
                existingMovie.copy(
                        title = updateDto.title ?: existingMovie.title,
                        synopsis = updateDto.synopsis ?: existingMovie.synopsis,
                        durationMin = updateDto.durationMin ?: existingMovie.durationMin,
                        releaseDate = updateDto.releaseDate ?: existingMovie.releaseDate,
                        status = updateDto.status ?: existingMovie.status,
                        posterUrl = updateDto.posterUrl ?: existingMovie.posterUrl,
                        trailerUrl = updateDto.trailerUrl ?: existingMovie.trailerUrl,
                        genres = genres,
                        formats = formats,
                        updatedAt = OffsetDateTime.now()
                )

        val savedMovie = movieRepository.save(updatedMovie)
        return movieMapper.movieToDto(savedMovie)
    }

    fun deleteMovie(id: Long): Boolean {
        val movie = movieRepository.findByIdOrNull(id) ?: return false
        val deactivatedMovie = movie.copy(active = false, updatedAt = OffsetDateTime.now())
        movieRepository.save(deactivatedMovie)
        return true
    }

    fun getAllMovies(): List<MovieDto> {
        return movieMapper.moviesToDtos(movieRepository.findAllWithGenresAndFormats())
    }

    fun getMoviesPaged(status: MovieStatus?, activeOnly: Boolean, pageable: Pageable): Page<MovieDto> {
        val page = when {
            status != null && activeOnly -> movieRepository.findByActiveTrueAndStatus(status, pageable)
            status != null && !activeOnly -> movieRepository.findByStatus(status, pageable)
            activeOnly -> movieRepository.findByActiveTrue(pageable)
            else -> movieRepository.findAll(pageable)
        }

        val ids = page.content.map { it.id }
        if (ids.isEmpty()) {
            return PageImpl(emptyList(), pageable, page.totalElements)
        }
        val hydrated = movieRepository.findByIdIn(ids)
        val byId = hydrated.associateBy { it.id }
        val ordered = ids.mapNotNull { byId[it] }
        val dtoList = movieMapper.moviesToDtos(ordered)
        return PageImpl(dtoList, pageable, page.totalElements)
    }
}
