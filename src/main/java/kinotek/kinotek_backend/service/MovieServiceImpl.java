package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.MovieDTO;
import kinotek.kinotek_backend.model.cinema.AgeRating;
import kinotek.kinotek_backend.model.cinema.Genre;
import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.repository.cinema.AgeRatingRepository;
import kinotek.kinotek_backend.repository.cinema.GenreRepository;
import kinotek.kinotek_backend.repository.cinema.MovieRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AgeRatingRepository ageRatingRepository;

    public MovieServiceImpl(MovieRepository movieRepository, GenreRepository genreRepository, AgeRatingRepository ageRatingRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.ageRatingRepository = ageRatingRepository;
    }

    @Override
    public List<MovieDTO> getMovies() {
        List<MovieDTO> result = new ArrayList<>();
        for (Movie movie : movieRepository.findAll()) {
            result.add(toDTO(movie));
        }
        return result;
    }

    @Override
    public List<MovieDTO> getNowPlayingMovies() {
        List<MovieDTO> result = new ArrayList<>();
        for (Movie movie : movieRepository.findNowPlaying(LocalDateTime.now())) {
            result.add(toDTO(movie));
        }
        return result;
    }

    @Override
    public MovieDTO getMovieById(int id) {
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie == null) {
            return null;
        }
        return toDTO(movie);
    }

    //id(0), så den starter på en ny film
    @Override
    public MovieDTO saveMovie(MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(0);
        return toDTO(movieRepository.save(movie));
    }

    @Override
    public MovieDTO updateMovie(int id, MovieDTO dto) {
        Movie movie = toEntity(dto);
        movie.setId(id);
        return toDTO(movieRepository.save(movie));
    }

    @Override
    public void deleteMovieById(int id) {
        movieRepository.deleteById(id);
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    @Override
    public List<AgeRating> ageRatings() {
        return ageRatingRepository.findAll();
    }

    //-------Mapper?------

    //DTO -> Entity
    private Movie toEntity(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setMovieName(dto.movieName());
        movie.setDuration(dto.duration());
        movie.setDescription(dto.description());
        movie.setImdbRef(dto.imdbRef());
        movie.setImageRef(dto.imageRef());
        movie.setAgeRating(ageRatingRepository.findById(dto.ageRatingId()).orElse(null));
        movie.setGenres(new HashSet<>(genreRepository.findAllById(dto.genreIds())));
        return movie;
    }

    //Entity -> DTO
    private MovieDTO toDTO(Movie movie) {
        List<Integer> genreIds = new ArrayList<>();
        List<String> genreNames = new ArrayList<>();
        for (Genre genre : movie.getGenres()) {
            genreIds.add(genre.getId());
            genreNames.add(genre.getGenreName());
        }

        int ageRatingId = 0;
        String ageRatingName = null;
        if (movie.getAgeRating() != null) {
            ageRatingId = movie.getAgeRating().getId();
            ageRatingName = movie.getAgeRating().getAgeRating();
        }

        return new MovieDTO(movie.getId(),
                movie.getMovieName(),
                movie.getDuration(),
                movie.getDescription(),
                movie.getImdbRef(),
                movie.getImageRef(),
                ageRatingId,
                genreIds,
                ageRatingName,
                genreNames);

    }
}