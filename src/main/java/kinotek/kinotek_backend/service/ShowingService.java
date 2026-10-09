package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.ShowingDTO;
import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;

import java.time.LocalDate;
import java.util.List;

import kinotek.kinotek_backend.dto.SeatMapDto;

public interface ShowingService {

    List<Showing> findAllShowing();
    Showing findShowingById(int id);
    List<Showing> findShowingByMovieAndDate(int movie_id, LocalDate dateToFind);
    List<Showing> findShowingByMovie(int movie_id);
    List<Showing> findUpcomingShowing();
    List<ShowingDTO> findUpcomingShowingByMovie(int movie_id);
    void saveShowing(ShowingDTO showingDTO);
    void deleteShowing(Showing showing);
    void deleteShowingById(int id);

}
