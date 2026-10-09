package kinotek.kinotek_backend.repository.cinema;

import kinotek.kinotek_backend.model.cinema.Auditorium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriumRepository extends JpaRepository<Auditorium, Integer> {


    Auditorium findAuditoriumByAuditoriumName(String auditorium);
}
