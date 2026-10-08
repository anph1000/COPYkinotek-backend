package kinotek.kinotek_backend.model.cinema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "seat", referencedColumnName = "seat_id")
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "showing", referencedColumnName = "showing_id")
    private Showing showing;

    @ManyToOne
    @JoinColumn(name = "invoice", referencedColumnName = "invoice_id")
    @JsonBackReference("invoice-bookings")
    private Invoice invoice;

    //GETTERS
    public int getId() {return id;}
    public Seat getSeat() {return seat;}
    public Showing getShowing() {return showing;}
    public Invoice getInvoice() {return invoice;}


    //SETTERS
    public void setId(int id) {this.id = id;}
    public void setSeat(Seat seat) {this.seat = seat;}
    public void setShowing(Showing showing) {this.showing = showing;}
    public void setInvoice(Invoice invoice) {this.invoice = invoice;}

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", seat=" + seat +
                ", showing=" + showing +
                ", invoice=" + invoice +
                '}';
    }
}
