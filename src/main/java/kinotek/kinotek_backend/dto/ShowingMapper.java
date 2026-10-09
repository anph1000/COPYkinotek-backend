package kinotek.kinotek_backend.dto;

import kinotek.kinotek_backend.model.cinema.Auditorium;
import kinotek.kinotek_backend.model.cinema.Movie;
import kinotek.kinotek_backend.model.cinema.Showing;
import org.springframework.stereotype.Component;

@Component
public class ShowingMapper {
    public ShowingDTO showingToDto(Showing showing){
        ShowingDTO showingDTO = new ShowingDTO();
        showingDTO.setId(showing.getId());
        showingDTO.setAuditorium(showing.getAuditorium().getAuditoriumName());
        showingDTO.setMovie(showing.getMovie().getMovieName());
        showingDTO.setDateTime(showing.getDateTime());
        return showingDTO;
    }

    public Showing DtoToShowing(ShowingDTO showingDTO, Movie movie, Auditorium auditorium){
        Showing showing = new Showing();
        showing.setDateTime(showingDTO.getDateTime());
        showing.setAuditorium(auditorium);
        showing.setMovie(movie);
        return showing;
    }

}
