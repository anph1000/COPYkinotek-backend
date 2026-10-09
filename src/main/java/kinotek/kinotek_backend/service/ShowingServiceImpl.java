package kinotek.kinotek_backend.service;

import kinotek.kinotek_backend.dto.SeatMapDto;
import kinotek.kinotek_backend.dto.SeatStatusDto;
import kinotek.kinotek_backend.dto.ShowingDTO;
import kinotek.kinotek_backend.dto.ShowingMapper;
import kinotek.kinotek_backend.model.cinema.*;
import kinotek.kinotek_backend.repository.cinema.BookingRepository;
import kinotek.kinotek_backend.repository.cinema.AuditoriumRepository;
import kinotek.kinotek_backend.repository.cinema.MovieRepository;
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
    private final MovieRepository movieRepository;
    private final AuditoriumRepository auditoriumRepository;
    private final ShowingMapper showingMapper;
    private final BookingRepository bookingRepository;


    public ShowingServiceImpl(ShowingRepository showingRepository, MovieRepository movieRepository, AuditoriumRepository auditoriumRepository, ShowingMapper showingMapper, BookingRepository bookingRepository) {

        this.showingRepository = showingRepository;
        this.movieRepository = movieRepository;
        this.auditoriumRepository = auditoriumRepository;
        this.showingMapper = showingMapper;
        this.bookingRepository = bookingRepository;
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
    public List<Showing> findShowingByMovieAndDate(int movie_id, LocalDate dateToFind){
        List<Showing> allShowings = showingRepository.findByMovie(movie_id);
        List<Showing> showingsToReturn = new ArrayList<>();

        for(Showing showing: allShowings){
            if(showing.getDateTime().toLocalDate().isEqual(dateToFind)){
                showingsToReturn.add(showing);
            }
        }

        return showingsToReturn;
    }

    @Override
    public List<Showing> findShowingByMovie(int movie_id){
        return showingRepository.findByMovie(movie_id);
    }

    @Override
    public List<Showing> findUpcomingShowing(){
        List<Showing> allShowings = showingRepository.findAll();
        return extractUpcomingShowing(allShowings);
    }

    @Override
    public List<ShowingDTO> findUpcomingShowingByMovie(int movie_id){
        List<Showing> foundShowings = extractUpcomingShowing(showingRepository.findByMovie(movie_id));
        List<ShowingDTO> showingDTOs = new ArrayList<>();
        for(Showing showing: foundShowings){
            showingDTOs.add(showingMapper.showingToDto(showing));
        }
        return showingDTOs;
    }

    private List<Showing> extractUpcomingShowing(List<Showing> showingsToPrune){
        List<Showing> showingsToReturn = new ArrayList<>();

        for(Showing showing: showingsToPrune){
            LocalDate showingDate = showing.getDateTime().toLocalDate();
            LocalDate currentDate = LocalDate.now();
            if(showingDate.isAfter(currentDate) || showingDate.isEqual(currentDate)){
                showingsToReturn.add(showing);
            }
        }
        return showingsToReturn;
    }

    @Override
    public void saveShowing(ShowingDTO showingDTO){
        Movie movie = movieRepository.findMovieByMovieName(showingDTO.getMovie());
        Auditorium auditorium = auditoriumRepository.findAuditoriumByAuditoriumName(showingDTO.getAuditorium());
        showingRepository.save(showingMapper.DtoToShowing(showingDTO, movie, auditorium));
    }

    @Override
    public void deleteShowing(Showing showing){
        showingRepository.delete(showing);
    }

    @Override
    public void deleteShowingById(int id){
        bookingRepository.deleteAll(bookingRepository.findByShowingId(id));
        showingRepository.deleteById(id);
    }

}
