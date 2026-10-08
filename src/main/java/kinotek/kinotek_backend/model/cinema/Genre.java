package kinotek.kinotek_backend.model.cinema;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Set;


@Entity
public class Genre {

    @Id
    @Column(name = "genre_id")
    private int id;
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Movie> movies;


    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}
