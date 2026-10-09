package kinotek.kinotek_backend.service;

import jakarta.annotation.Nullable;
import kinotek.kinotek_backend.dto.BookingConfirmationDto;
import kinotek.kinotek_backend.dto.BookingRequestDto;
import kinotek.kinotek_backend.dto.SeatMapDto;
import kinotek.kinotek_backend.dto.SeatStatusDto;
import kinotek.kinotek_backend.model.cinema.*;
import kinotek.kinotek_backend.model.user.Customer;
import kinotek.kinotek_backend.repository.cinema.BookingRepository;
import kinotek.kinotek_backend.repository.cinema.InvoiceRepository;
import kinotek.kinotek_backend.repository.cinema.SeatRepository;
import kinotek.kinotek_backend.repository.cinema.ShowingRepository;
import kinotek.kinotek_backend.repository.user.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ShowingService showingService;

    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final SeatService seatService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ShowingService showingService,
                              CustomerService customerService,
                              InvoiceService invoiceService, SeatService seatService) {
        this.bookingRepository = bookingRepository;
        this.showingService = showingService;
        this.customerService = customerService;
        this.invoiceService = invoiceService;
        this.seatService = seatService;
    }

    @Override
    public List<Booking> getBookings() {
        return bookingRepository.findAll();

    }

    @Override
    public Set<Integer> bookedSeatIdsByShowingId(int showingId) {
        List<Booking> bookedSeats = bookingRepository.findByShowingId(showingId);
        Set<Integer> bookedSeatIds = new HashSet<>();

        for (Booking b : bookedSeats) {
            bookedSeatIds.add(b.getSeat().getId());
        }

        return bookedSeatIds;
    }

    @Transactional(readOnly = true)
    @Override
    public SeatMapDto getSeatMap(int showingId) {
        Showing showing = showingService.findShowingById(showingId);

        Set<Integer> bookedSeatIds = bookedSeatIdsByShowingId(showingId);

        Auditorium auditorium = showing.getAuditorium();

        List<SeatStatusDto> seats = new ArrayList<>();

        for(SeatRow row : auditorium.getRows()) {
            for (Seat seat : row.getSeats()) {
                seats.add(new SeatStatusDto(
                        seat.getId(),
                        row.getId(),
                        row.getRowLetter(),
                        seat.getSeatNumber(),
                        seat.isAccessible(),
                        bookedSeatIds.contains(seat.getId())
                ));
            }
        }

        return new SeatMapDto(
                auditorium.getId(),
                auditorium.getAuditoriumName(),
                showing.getMovie().getMovieName(),
                showing.getDateTime(),
                seats
        );
    }

    private void validateAtLeastOneChosenSeat(Set<Integer> seatIds) {
        //VALIDATE AT LEAST ONE SEAT HAS BEEN CHOSEN
        if (seatIds == null || seatIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vælg mindst ét sæde");
        }
    }

    private void validateShowingHasNotStarted(int showingId) {
        Showing showing = showingService.findShowingById(showingId);
        if (showing.getDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visningen er begyndt");
        }
    }

    private void validateEmail(String email) {
        if (email != null && !email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ugyldig email"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingConfirmationDto getBookingConfirmation(int invoiceId) {
        Invoice invoice = invoiceService.findInvoiceById(invoiceId);
        List<Booking> bookings = bookingRepository.findByInvoiceId(invoiceId);
        Showing showing = showingService.findShowingById(bookings.getFirst().getShowing().getId());
        List<SeatStatusDto> bookedSeatDtoList = new ArrayList<>();

        for (Booking b : bookings) {
            bookedSeatDtoList.add(new SeatStatusDto(
                    b.getSeat().getId(),
                    b.getSeat().getRow().getId(),
                    b.getSeat().getRow().getRowLetter(),
                    b.getSeat().getSeatNumber(),
                    b.getSeat().isAccessible(),
                    true
            ));
        }

        return new BookingConfirmationDto(
                invoice.getId(),
                showing.getId(),
                showing.getAuditorium().getAuditoriumName(),
                showing.getMovie().getMovieName(),
                showing.getDateTime(),
                bookedSeatDtoList,
                invoice.getPurchaseTime()
        );
    }



    @Override
    @Transactional
    public int createBookings(BookingRequestDto bookingRequestDto) {

        validateAtLeastOneChosenSeat(bookingRequestDto.seatIds());
        validateShowingHasNotStarted(bookingRequestDto.showingId());
        validateEmail(bookingRequestDto.email());


        Customer customer = new Customer();

        String guestEmail = bookingRequestDto.email();

        if (customerService.existsByEmail(guestEmail)) {
            customer = customerService.findCustomerByEmail(guestEmail);
        } else {
            customer = customerService.saveGuestByEmail(guestEmail);
        }


        //CREATE INVOICE
        Invoice invoice = invoiceService.createInvoice(customer);

        Showing showing = showingService.findShowingById(bookingRequestDto.showingId());

        List<Booking> bookings = new ArrayList<>();



        for (int seatId : bookingRequestDto.seatIds()) {
            if (bookingRepository.existsByShowingIdAndSeatId(showing.getId(), seatId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Sæde " + seatId + " er allerede booket");
            }
            Booking booking = new Booking();
            Seat seat = seatService.findBySeatId(seatId);
            booking.setSeat(seat);
            booking.setShowing(showing);
            booking.setInvoice(invoice);
            bookings.add(booking);
        }

        bookingRepository.saveAll(bookings);

        return invoice.getId();
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
