package kinotek.kinotek_backend.service;
import kinotek.kinotek_backend.model.cinema.Seat;
import kinotek.kinotek_backend.repository.cinema.SeatRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public Seat findBySeatId(int id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sædet " + id + " findes ikke"));
    }
}
