package kinotek.kinotek_backend.model.cinema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
public class Auditorium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditorium_id")
    private int id;
    private String auditoriumName;

    @OneToMany(mappedBy = "auditorium",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rowLetter ASC")
    @JsonManagedReference("auditorium-rows")
    private List<SeatRow> rows = new ArrayList<>();

    @OneToMany(mappedBy = "auditorium")
    @JsonIgnore
    private Set<Showing> showings;

    public void addRow(SeatRow row) {
        rows.add(row);
        row.setAuditorium(this);
    }

    public Auditorium(int id, String auditoriumName, List<SeatRow> rows) {
        this.id = id;
        this.auditoriumName = auditoriumName;
        this.rows = rows;
    }

    public Auditorium() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuditoriumName() {
        return auditoriumName;
    }

    public void setAuditoriumName(String auditoriumName) {
        this.auditoriumName = auditoriumName;
    }

    public List<SeatRow> getRows() {
        return rows;
    }

    public void setRows(List<SeatRow> rows) {
        this.rows = rows;
    }


    public Set<Showing> getShowings() {
        return showings;
    }

    public void setShowings(Set<Showing> showings) {
        this.showings = showings;
    }
}
