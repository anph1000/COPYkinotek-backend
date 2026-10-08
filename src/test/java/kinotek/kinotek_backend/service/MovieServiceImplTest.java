package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.MovieDTO;
import kinotek.kinotek_backend.model.cinema.AgeRating;
import kinotek.kinotek_backend.model.cinema.Genre;
import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.repository.cinema.AgeRatingRepository;
import kinotek.kinotek_backend.repository.cinema.GenreRepository;
import kinotek.kinotek_backend.repository.cinema.MovieRepository;
import kinotek.kinotek_backend.repository.cinema.ShowingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    private MovieRepository movieRepository;
    private GenreRepository genreRepository;
    private AgeRatingRepository ageRatingRepository;
    private ShowingRepository showingRepository;
    private MovieServiceImpl movieService;

    private final AgeRating rating11 = ageRating(3, "11");
    private final Genre action = genre(1, "Action");
    private final Genre drama = genre(2, "Drama");

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        genreRepository = mock(GenreRepository.class);
        ageRatingRepository = mock(AgeRatingRepository.class);
        showingRepository = mock(ShowingRepository.class);
        movieService = new MovieServiceImpl(movieRepository, genreRepository, ageRatingRepository, showingRepository);

        when(ageRatingRepository.findById(3)).thenReturn(Optional.of(rating11));
        when(genreRepository.findAllById(List.of(1, 2))).thenReturn(List.of(action, drama));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void getMovieByIdReturnsNamesForAgeRatingAndGenres() {
        when(movieRepository.findById(7)).thenReturn(Optional.of(movie(7, "Testfilm", rating11, Set.of(drama, action))));

        MovieDTO dto = movieService.getMovieById(7);

        assertEquals("Testfilm", dto.movieName());
        assertEquals(3, dto.ageRatingId());
        assertEquals("11", dto.ageRating());
        assertEquals(List.of(1, 2), dto.genreIds());
        assertEquals(List.of("Action", "Drama"), dto.genres());
    }

    @Test
    void getMovieByIdHandlesMissingAgeRating() {
        when(movieRepository.findById(7)).thenReturn(Optional.of(movie(7, "Uden rating", null, Set.of())));

        MovieDTO dto = movieService.getMovieById(7);

        assertEquals(0, dto.ageRatingId());
        assertNull(dto.ageRating());
    }

    @Test
    void getMovieByIdThrowsNotFoundForUnknownId() {
        when(movieRepository.findById(99)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> movieService.getMovieById(99));
    }

    @Test
    void saveMovieMapsDtoToNewEntity() {
        MovieDTO saved = movieService.saveMovie(input(" Ny film ", 110, 3, List.of(1, 2)));

        assertEquals("Ny film", saved.movieName());
        assertEquals(110, saved.duration());
        assertEquals("11", saved.ageRating());
        assertEquals(List.of("Action", "Drama"), saved.genres());
    }

    @Test
    void saveMovieRejectsBlankTitle() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> movieService.saveMovie(input("  ", 110, 3, List.of())));
    }

    @Test
    void saveMovieRejectsUnknownAgeRating() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> movieService.saveMovie(input("Film", 110, 42, List.of())));
    }

    @Test
    void updateMovieKeepsIdAndChangesFields() {
        Movie existing = movie(5, "Gammel titel", rating11, Set.of(action));
        when(movieRepository.findById(5)).thenReturn(Optional.of(existing));

        MovieDTO updated = movieService.updateMovie(5, input("Ny titel", 95, 3, List.of(1, 2)));

        assertEquals(5, updated.id());
        assertEquals("Ny titel", updated.movieName());
        assertEquals(List.of(1, 2), updated.genreIds());
    }

    @Test
    void updateMovieThrowsNotFoundForUnknownId() {
        when(movieRepository.findById(99)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> movieService.updateMovie(99, input("Film", 90, 3, List.of())));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void deleteMovieDeletesWhenNoShowings() {
        Movie existing = movie(5, "Film", rating11, Set.of());
        when(movieRepository.findById(5)).thenReturn(Optional.of(existing));
        when(showingRepository.existsByMovieId(5)).thenReturn(false);

        movieService.deleteMovieById(5);

        verify(movieRepository).delete(existing);
    }

    @Test
    void deleteMovieIsRejectedWhenItHasShowings() {
        when(movieRepository.findById(5)).thenReturn(Optional.of(movie(5, "Film", rating11, Set.of())));
        when(showingRepository.existsByMovieId(5)).thenReturn(true);

        assertStatus(HttpStatus.CONFLICT, () -> movieService.deleteMovieById(5));
        verify(movieRepository, never()).delete(any());
    }

    private static void assertStatus(HttpStatus expected, Runnable action) {
        ResponseStatusException e = assertThrows(ResponseStatusException.class, action::run);
        assertEquals(expected, e.getStatusCode());
    }

    private static MovieDTO input(String name, int duration, int ageRatingId, List<Integer> genreIds) {
        return new MovieDTO(null, name, duration, "Beskrivelse", null, null, ageRatingId, genreIds, null, null);
    }

    private static Movie movie(int id, String name, AgeRating ageRating, Set<Genre> genres) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setMovieName(name);
        movie.setDuration(100);
        movie.setAgeRating(ageRating);
        movie.setGenres(genres);
        return movie;
    }

    private static AgeRating ageRating(int id, String name) {
        AgeRating ageRating = new AgeRating();
        ageRating.setId(id);
        ageRating.setAgeRating(name);
        return ageRating;
    }

    private static Genre genre(int id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setGenreName(name);
        return genre;
    }
}
