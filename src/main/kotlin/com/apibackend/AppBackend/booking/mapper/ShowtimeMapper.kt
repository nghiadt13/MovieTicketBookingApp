package com.apibackend.AppBackend.booking.mapper

import com.apibackend.AppBackend.booking.dto.*
import com.apibackend.AppBackend.booking.model.Seat
import com.apibackend.AppBackend.homepage.model.Format
import com.apibackend.AppBackend.homepage.model.Genre
import com.apibackend.AppBackend.homepage.model.Movie
import com.apibackend.AppBackend.homepage.model.Screen
import com.apibackend.AppBackend.homepage.model.Showtime
import org.springframework.stereotype.Component

@Component
class ShowtimeMapper {

    fun toShowtimeDto(showtime: Showtime): ShowtimeDto =
            ShowtimeDto(
                    id = showtime.id,
                    movieId = showtime.movie.id,
                    screenId = showtime.screen.id,
                    startTime = showtime.startTime,
                    endTime = showtime.endTime,
                    status = showtime.status,
                    availableSeats = showtime.availableSeats,
                    isActive = showtime.active,
                    createdAt = showtime.createdAt,
                    updatedAt = showtime.updatedAt
            )

    fun toMovieDetailDto(movie: Movie): MovieDetailDto =
            MovieDetailDto(
                    id = movie.id,
                    title = movie.title,
                    synopsis = movie.synopsis,
                    durationMin = movie.durationMin,
                    releaseDate = movie.releaseDate,
                    status = movie.status,
                    posterUrl = movie.posterUrl,
                    trailerUrl = movie.trailerUrl,
                    ratingAvg = movie.ratingAvg,
                    ratingCount = movie.ratingCount,
                    isActive = movie.active,
                    createdAt = movie.createdAt,
                    updatedAt = movie.updatedAt,
                    genres = movie.genres.map { toGenreDto(it) },
                    formats = movie.formats.map { toFormatDto(it) }
            )

    fun toScreenDetailDto(screen: Screen): ScreenDetailDto =
            ScreenDetailDto(
                    id = screen.id,
                    cinemaId = screen.cinema.id,
                    cinemaName = screen.cinema.name,
                    name = screen.name,
                    totalSeats = screen.totalSeats,
                    screenType = screen.screenType,
                    isActive = screen.active,
                    createdAt = screen.createdAt,
                    updatedAt = screen.updatedAt
            )

    fun toSeatDto(seat: Seat): SeatDto =
            SeatDto(
                    id = seat.id,
                    screenId = seat.screen.id,
                    rowName = seat.rowName,
                    seatNumber = seat.seatNumber,
                    seatType = seat.seatType,
                    isActive = seat.active,
                    createdAt = seat.createdAt
            )

    fun toGenreDto(genre: Genre): GenreDto =
            GenreDto(id = genre.id, name = genre.name, slug = genre.slug)

    fun toFormatDto(format: Format): FormatDto =
            FormatDto(id = format.id, code = format.code, label = format.label)

    fun toShowtimeDetailDto(showtime: Showtime, seats: List<Seat>): ShowtimeDetailDto =
            ShowtimeDetailDto(
                    showtime = toShowtimeDto(showtime),
                    movie = toMovieDetailDto(showtime.movie),
                    screen = toScreenDetailDto(showtime.screen),
                    seats = seats.map { toSeatDto(it) }
            )
}
