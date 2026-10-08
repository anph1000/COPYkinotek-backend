package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.MovieDTO;
import kinotek.kinotek_backend.dto.MovieShowingDto;
import kinotek.kinotek_backend.model.cinema.AgeRating;
import kinotek.kinotek_backend.model.cinema.Genre;
import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;
import kinotek.kinotek_backend.repository.cinema.AgeRatingRepository;
import kinotek.kinotek_backend.repository.cinema.GenreRepository;
import kinotek.kinotek_backend.repository.cinema.MovieRepository;
import kinotek.kinotek_backend.repository.cinema.ShowingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final AgeRatingRepository ageRatingRepository;
    private final ShowingRepository showingRepository;

    public MovieServiceImpl(MovieRepository movieRepository, GenreRepository genreRepository,
                            AgeRatingRepository ageRatingRepository, ShowingRepository showingRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.ageRatingRepository = ageRatingRepository;
        this.showingRepository = showingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDTO> getMovies() {
        List<MovieDTO> result = new ArrayList<>();
        for(Movie movie : movieRepository.findAll()) {
            result.add(toDTO(movie));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDTO getMovieById(int id) {
        return toDTO(findMovie(id));
    }

    //id(0), så den starter på en ny film
    @Override
    public MovieDTO saveMovie(MovieDTO dto) {
        Movie movie = new Movie();
        applyDTO(movie, dto);
        movie.setId(0);
        return toDTO(movieRepository.save(movie));
    }

    // henter den eksisterende film, så id og forestillinger bevares
    @Override
    public MovieDTO updateMovie(int id, MovieDTO dto) {
        Movie movie = findMovie(id);
        applyDTO(movie, dto);
        return toDTO(movieRepository.save(movie));
    }

    // en film med forestillinger kan ikke slettes, da bookinger peger på forestillingerne
    @Override
    public void deleteMovieById(int id) {
        Movie movie = findMovie(id);
        if (showingRepository.existsByMovieId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Filmen har forestillinger og kan ikke slettes. Slet forestillingerne først.");
        }
        movieRepository.delete(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieShowingDto> getUpcomingShowings(int movieId) {
        findMovie(movieId);
        List<MovieShowingDto> result = new ArrayList<>();
        for (Showing showing : showingRepository.findByMovieIdAndDateTimeGreaterThanEqualOrderByDateTime(
                movieId, LocalDate.now().atStartOfDay())) {
            result.add(new MovieShowingDto(
                    showing.getId(),
                    showing.getDateTime(),
                    showing.getAuditorium().getAuditoriumName()));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgeRating> ageRatings() {
        return ageRatingRepository.findAll();
    }

    private Movie findMovie(int id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filmen findes ikke"));
    }

    //-------Mapper?------

    //DTO -> Entity
    private void applyDTO(Movie movie, MovieDTO dto) {
        if (dto.movieName() == null || dto.movieName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filmen skal have en titel");
        }
        if (dto.duration() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spilletid skal være over 0 minutter");
        }
        movie.setMovieName(dto.movieName().trim());
        movie.setDuration(dto.duration());
        movie.setDescription(dto.description());
        movie.setImdbRef(dto.imdbRef());
        movie.setImageRef(dto.imageRef());
        movie.setAgeRating(ageRatingRepository.findById(dto.ageRatingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ukendt aldersgrænse")));

        List<Integer> genreIds = dto.genreIds() == null ? List.of() : dto.genreIds();
        List<Genre> genres = genreRepository.findAllById(genreIds);
        if (genres.size() != new HashSet<>(genreIds).size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ukendt genre");
        }
        movie.setGenres(new HashSet<>(genres));
    }

    //Entity -> DTO
    private MovieDTO toDTO(Movie movie) {
        List<Genre> sortedGenres = new ArrayList<>(movie.getGenres() == null ? List.of() : movie.getGenres());
        sortedGenres.sort(Comparator.comparing(Genre::getGenreName));

        List<Integer> genreIds = new ArrayList<>();
        List<String> genreNames = new ArrayList<>();
        for (Genre genre : sortedGenres) {
            genreIds.add(genre.getId());
            genreNames.add(genre.getGenreName());
        }

        int ageRatingId = 0;
        String ageRating = null;
        if (movie.getAgeRating() != null) {
            ageRatingId = movie.getAgeRating().getId();
            ageRating = movie.getAgeRating().getAgeRating();
        }

        return new MovieDTO(
                movie.getId(),
                movie.getMovieName(),
                movie.getDuration(),
                movie.getDescription(),
                movie.getImdbRef(),
                movie.getImageRef(),
                ageRatingId,
                genreIds,
                ageRating,
                genreNames
        );
    }
}
