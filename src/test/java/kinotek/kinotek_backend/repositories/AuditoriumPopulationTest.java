package kinotek.kinotek_backend.repositories;

import kinotek.kinotek_backend.model.cinema.Auditorium;
import kinotek.kinotek_backend.model.cinema.Seat;
import kinotek.kinotek_backend.model.cinema.SeatRow;
import kinotek.kinotek_backend.repository.cinema.AuditoriumRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class AuditoriumPopulationTest {

    private static final int ROW_COUNT = 8;
    private static final int SEATS_PER_ROW = 12;

    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesAuditoriumWithRowsAndSeats() {
        Auditorium saved = auditoriumRepository.save(buildAuditorium("Sal 1"));
        reloadFromDatabase();

        Auditorium found = auditoriumRepository.findById(saved.getId()).orElseThrow();

        assertEquals(ROW_COUNT, found.getRows().size());
        assertEquals(ROW_COUNT * SEATS_PER_ROW, countSeats(found));
    }

    private void reloadFromDatabase() {
        entityManager.flush();
        entityManager.clear();
    }

    private Auditorium buildAuditorium(String name) {
        Auditorium auditorium = new Auditorium();
        auditorium.setAuditoriumName(name);
        for (int i = 0; i < ROW_COUNT; i++) {
            auditorium.addRow(buildRow(rowLetter(i)));
        }
        return auditorium;
    }

    private SeatRow buildRow(String letter) {
        SeatRow row = new SeatRow();
        row.setRowLetter(letter);
        for (int number = 1; number <= SEATS_PER_ROW; number++) {
            row.addSeat(buildSeat(number));
        }
        return row;
    }

    private Seat buildSeat(int number) {
        Seat seat = new Seat();
        seat.setSeatNumber(number);
        return seat;
    }

    private String rowLetter(int index) {
        return String.valueOf((char) ('A' + index));
    }

    private int countSeats(Auditorium auditorium) {
        return auditorium.getRows().stream().mapToInt(r -> r.getSeats().size()).sum();
    }
}