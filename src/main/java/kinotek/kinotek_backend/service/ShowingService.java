package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;

import java.time.LocalDate;
import java.util.List;

import kinotek.kinotek_backend.dto.SeatMapDto;

public interface ShowingService {

    List<Showing> findAllShowing();
    Showing findShowingById(int id);
    List<Showing> findShowingByMovieAndDate(Movie movie, LocalDate dateToFind);
    List<Showing> findShowingByMovie(Movie movie);
    List<Showing> findUpcomingShowing();
    void saveShowing(Showing showing);
    void deleteShowing(Showing showing);
    void deleteShowingById(int id);

    public SeatMapDto getSeatMap(int showingId);

}
