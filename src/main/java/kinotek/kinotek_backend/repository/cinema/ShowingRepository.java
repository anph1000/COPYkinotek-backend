package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ShowingRepository extends JpaRepository<Showing, Integer> {

    @NativeQuery("select * from Showing showing where showing.movie = ?1")
    List<Showing> findByMovie(Movie movie);
}
