package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByShowingId(int showingId);
    boolean existsByShowingIdAndSeatId(int showingId, int seatId);

}
