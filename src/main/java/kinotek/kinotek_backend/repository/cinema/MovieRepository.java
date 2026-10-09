package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Integer> {

    @Query("SELECT DISTINCT m FROM Movie m JOIN m.showings s WHERE s.dateTime >= :now")
    List<Movie> findNowPlaying(@Param("now") LocalDateTime now);

    Movie findMovieByMovieName(String movie);
}
