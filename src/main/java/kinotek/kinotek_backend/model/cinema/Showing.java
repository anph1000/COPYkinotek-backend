package kinotek.kinotek_backend.model.cinema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Showing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showing_id")
    private int id;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "auditorium", referencedColumnName = "auditorium_id")
    private Auditorium  auditorium;

    @ManyToOne
    @JoinColumn(name = "movie", referencedColumnName = "movie_id")
    @JsonManagedReference
    private Movie movie;


    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        return "Showing{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", auditorium=" + auditorium +
                ", movie=" + movie +
                '}';
    }
}
