package kinotek.kinotek_backend.service;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import kinotek.kinotek_backend.model.cinema.Booking;
import kinotek.kinotek_backend.model.cinema.Invoice;
import kinotek.kinotek_backend.model.cinema.Seat;
import kinotek.kinotek_backend.model.cinema.Showing;
import kinotek.kinotek_backend.model.user.Customer;
import kinotek.kinotek_backend.repository.cinema.BookingRepository;
import kinotek.kinotek_backend.repository.cinema.InvoiceRepository;
import kinotek.kinotek_backend.repository.cinema.SeatRepository;
import kinotek.kinotek_backend.repository.cinema.ShowingRepository;
import kinotek.kinotek_backend.repository.user.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private ShowingRepository showingRepository;
    private SeatRepository seatRepository;
    private CustomerRepository customerRepository;
    private InvoiceRepository invoiceRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ShowingRepository showingRepository,
                              SeatRepository seatRepository,
                              CustomerRepository customerRepository,
                              InvoiceRepository invoiceRepository) {

        this.bookingRepository = bookingRepository;
        this.showingRepository = showingRepository;
        this.seatRepository = seatRepository;
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Booking> getBookings() {
        return bookingRepository.findAll();

    }

    public Set<Integer> bookedSeatIdsByShowingId(int showingId) {
        List<Booking> bookedSeats = bookingRepository.findByShowingId(showingId);
        Set<Integer> bookedSeatIds = new HashSet<>();

        for (Booking b : bookedSeats) {
            bookedSeatIds.add(b.getId());
        }

        return bookedSeatIds;
    }


    @Transactional
    public Map<String, Object> createBookings(int showingId, Set<Integer> seatIds,
                                              @Nullable String email, Integer phoneNumber) {

        //VALIDATE AT LEAST ONE SEAT HAS BEEN CHOSEN
        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vælg mindst ét sæde");
        }


        //VALIDATE SHOWING HASN'T STARTED
        Showing showing = showingRepository.findById(showingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visningen findes ikke"));
        if (showing.getDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visningen er begyndt");
        }

        // VALIDATE EMAIL
        if (email != null && !email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ugyldig email"
            );
        }

        //VALIDATE PHONE NUMBER
        if (phoneNumber != null && (phoneNumber < 10000000 || phoneNumber > 99999999)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ugyldigt telefonnummer"
            );
        }


        //VALIDATE SEATS AND CREATE INVOICE
        Invoice invoice = new Invoice();
        invoice.setPurchaseTime(LocalDateTime.now());

        for (int seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sæde " + seatId + " findes ikke"));

            if (bookingRepository.existsByShowingIdAndSeatId(showing.getId(), seatId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Sæde " + seatId + " er allerede booket");
            }


            Booking booking = new Booking();
            booking.setSeat(seat);
            booking.setShowing(showing);
            booking.setInvoice(invoice);
            invoice.getBookings().add(booking);
        }

        Invoice saved = invoiceRepository.save(invoice);

        Map<String, Object> result = new HashMap<>();

        result.put("invoiceId", saved.getId());
        result.put("showingId", showing.getId());
        result.put("seatIds", seatIds);
        result.put("purchaseTime", saved.getPurchaseTime());

        return result;


    }


//    @Override
//    @Transactional
//    public BookingConfirmation createBookings(CreateBookingRequest request) {
//
//        //VALIDER AT LEAST ONE SEAT HAS BEEN CHOSEN
//        Set<Integer> seatIds = request.seatIds();
//        if (seatIds == null || seatIds.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vælg mindst ét sæde");
//        }
//
//
//        //VALIDATE SHOWING HASN'T STARTED
//        Showing showing = showingRepository.findById(request.showingId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visningen findes ikke"));
//        if (showing.getDateTime().isBefore(LocalDateTime.now())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visningen er begyndt");
//        }
//
//        Invoice invoice = new Invoice();
//        invoice.setPurchaseTime(LocalDateTime.now());
//
//        for (int seatId : seatIds) {
//            Seat seat = seatRepository.findById(seatId)
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sæde " + seatId + " findes ikke"));
//
//            if (bookingRepository.existsByShowingIdAndSeatId(showing.getId(), seatId)) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT, "Sæde " + seatId + " er allerede booket");
//            }
//
//
//            Booking booking = new Booking();
//            booking.setSeat(seat);
//            booking.setShowing(showing);
//            booking.setInvoice(invoice);
//            invoice.getBookings().add(booking);
//        }
//
//        Invoice saved = invoiceRepository.save(invoice);
//
//        return new BookingConfirmation(saved.getId(), showing.getId(), seatIds, saved.getPurchaseTime());
//
//
//    }


}
