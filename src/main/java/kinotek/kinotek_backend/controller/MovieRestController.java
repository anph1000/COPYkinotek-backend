package kinotek.kinotek_backend.controller;


import kinotek.kinotek_backend.dto.MovieDTO;
import kinotek.kinotek_backend.model.cinema.AgeRating;
import kinotek.kinotek_backend.model.cinema.Genre;
import kinotek.kinotek_backend.service.MovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieRestController {

    private final MovieService movieService;

    public MovieRestController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping()
    public List<MovieDTO> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/{id}")
    public MovieDTO getMovieById(@PathVariable int id) {
        return movieService.getMovieById(id);
    }

    @PostMapping()
    public MovieDTO saveMovie(@RequestBody MovieDTO dto) {
        return movieService.saveMovie(dto);
    }

    @PostMapping("/{id}/update")
    public MovieDTO updateMovie(@PathVariable int id, @RequestBody MovieDTO dto) {
        return movieService.updateMovie(id, dto);
    }

    @PostMapping("/{id}/delete")
    public void deleteMovieById(@PathVariable int id) {
        movieService.deleteMovieById(id);
    }

    @GetMapping("/genres")
    public List<Genre> getGenres() {
        return movieService.getGenres();
    }

    @GetMapping("/age-ratings")
    public List<AgeRating> ageRatings() {
        return movieService.ageRatings();
    }

}
