package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShowingRepository extends JpaRepository<Showing, Integer> {

    //@NativeQuery("select * from Showing as showing where showing.movie_id = ?1")
    //List<Showing> findByMovie(int movie_id);

    @Query("FROM Showing showing where showing.movie.id = :movie_id")
    List<Showing> findByMovie(@Param("movie_id") int movie_id);
}
