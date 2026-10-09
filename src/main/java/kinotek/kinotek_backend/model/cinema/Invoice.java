package kinotek.kinotek_backend.model.cinema;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import kinotek.kinotek_backend.model.user.Customer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "customer", referencedColumnName = "customer_id")
    @JsonManagedReference
    private Customer customer;
    private LocalDateTime purchaseTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
    @JsonManagedReference("invoice-bookings")
    private Set<Booking> bookings = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDateTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customer=" + customer +
                ", purchaseTime=" + purchaseTime +
                ", bookings=" + bookings +
                '}';
    }

    public void set() {
        
    }
}
