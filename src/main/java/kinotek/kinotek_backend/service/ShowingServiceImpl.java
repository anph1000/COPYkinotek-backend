package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.SeatMapDto;
import kinotek.kinotek_backend.dto.SeatStatusDto;
import kinotek.kinotek_backend.model.cinema.*;
import kinotek.kinotek_backend.repository.cinema.ShowingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShowingServiceImpl implements ShowingService{
    private final ShowingRepository showingRepository;
    private final BookingService bookingService;


    public ShowingServiceImpl(ShowingRepository showingRepository, BookingService bookingService) {
        this.showingRepository = showingRepository;
        this.bookingService = bookingService;
    }

    @Transactional(readOnly = true)
    public SeatMapDto getSeatMap(int showingId) {
        Showing showing = findShowingById(showingId);

        Set<Integer> bookedSeatIds = bookingService.bookedSeatIdsByShowingId(showingId);

        Auditorium auditorium = showing.getAuditorium();

        List<SeatStatusDto> seats = new ArrayList<>();

        for(SeatRow row : auditorium.getRows()) {
            for (Seat seat : row.getSeats()) {
                seats.add(new SeatStatusDto(
                        seat.getId(),
                        row.getId(),
                        row.getRowLetter(),
                        seat.getSeatNumber(),
                        seat.isAccessible(),
                        bookedSeatIds.contains(seat.getId())
                ));
            }
        }

        return new SeatMapDto(
                auditorium.getId(),
                auditorium.getAuditoriumName(),
                showing.getMovie().getMovieName(),
                showing.getDateTime(),
                seats
        );
    }

    @Override
    public List<Showing> findAllShowing(){
        return showingRepository.findAll();
    }

    @Override
    public Showing findShowingById(int id){
        return showingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filmvisningen findes ikke"));
    }

    @Override
    public List<Showing> findShowingByMovieAndDate(Movie movie, LocalDate dateToFind){
        List<Showing> allShowings = showingRepository.findByMovie(movie);
        List<Showing> showingsToReturn = new ArrayList<>();

        for(Showing showing: allShowings){
            if(showing.getDateTime().toLocalDate().isEqual(dateToFind)){
                showingsToReturn.add(showing);
            }
        }

        return showingsToReturn;
    }

    @Override
    public List<Showing> findShowingByMovie(Movie movie){
        return showingRepository.findByMovie(movie);
    }

    @Override
    public List<Showing> findUpcomingShowing(){
        List<Showing> allShowings = showingRepository.findAll();
        List<Showing> showingsToReturn = new ArrayList<>();

        for(Showing showing: allShowings){
            LocalDate showingDate = showing.getDateTime().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            if(showingDate.isAfter(currentDate) || showingDate.isEqual(currentDate)){
                showingsToReturn.add(showing);
            }
        }
        return showingsToReturn;
    }

    @Override
    public void saveShowing(Showing showing){
        showingRepository.save(showing);
    }

    @Override
    public void deleteShowing(Showing showing){
        showingRepository.delete(showing);
    }

    @Override
    public void deleteShowingById(int id){
        deleteShowing(findShowingById(id));
    }

}
