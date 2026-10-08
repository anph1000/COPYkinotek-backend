package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.MovieDTO;
import kinotek.kinotek_backend.dto.MovieShowingDto;
import kinotek.kinotek_backend.model.cinema.AgeRating;
import kinotek.kinotek_backend.model.cinema.Genre;

import java.util.List;

public interface MovieService {

    List<MovieDTO> getMovies();
    MovieDTO getMovieById(int id);
    MovieDTO saveMovie(MovieDTO dto);
    MovieDTO updateMovie(int id,MovieDTO dto);
    void deleteMovieById(int id);
    List<MovieShowingDto> getUpcomingShowings(int movieId);
    List<Genre> getGenres();
    List<AgeRating> ageRatings();

}
