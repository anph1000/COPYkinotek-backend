package kinotek.kinotek_backend.controller;

import kinotek.kinotek_backend.dto.ShowingDTO;
import kinotek.kinotek_backend.service.ShowingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/showing")
@CrossOrigin("*")
public class ShowingRestController {

    private final ShowingService showingService;

    public ShowingRestController(ShowingService showingService) {
        this.showingService = showingService;
    }

    @GetMapping("/{movie_id}")
    @ResponseBody
    public List<ShowingDTO> upcomingShowingsByMovieId(@PathVariable int movie_id){
        return showingService.findUpcomingShowingByMovie(movie_id);
    }

    @PostMapping("/{id}/delete")
    public void deleteShowingId(@PathVariable int id) {
        showingService.deleteShowingById(id);
    }

    @PostMapping("/create-showing")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveShowing(@RequestBody ShowingDTO showingDTO){
        showingService.saveShowing(showingDTO);
        return "Showing created";
    }
    
}
