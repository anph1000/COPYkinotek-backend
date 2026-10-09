package kinotek.kinotek_backend.controller;

import jakarta.annotation.Nullable;
import kinotek.kinotek_backend.dto.BookingConfirmationDto;
import kinotek.kinotek_backend.dto.BookingRequestDto;
import kinotek.kinotek_backend.dto.SeatMapDto;
import kinotek.kinotek_backend.model.cinema.Booking;
import kinotek.kinotek_backend.model.cinema.Seat;
import kinotek.kinotek_backend.model.cinema.Showing;
import kinotek.kinotek_backend.service.BookingService;

import kinotek.kinotek_backend.service.SeatService;
import kinotek.kinotek_backend.service.ShowingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("")
public class BookingRestController {

    private final ShowingService showingService;
    private final BookingService bookingService;

    public BookingRestController(ShowingService showingService, BookingService bookingService){
        this.showingService = showingService;
        this.bookingService = bookingService;
    }

    @GetMapping("/api/showing/{showingId}/seat-map")
    public SeatMapDto getSeatMap(@PathVariable int showingId) {
        return bookingService.getSeatMap(showingId);
    }

    @PostMapping("/api/bookings/create-booking-request")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingConfirmationDto createBooking(@RequestBody BookingRequestDto bookingRequestDto) {
        int invoiceId = bookingService.createBookings(bookingRequestDto);
        return bookingService.getBookingConfirmation(invoiceId);
    }

    @GetMapping("api/bookings/{invoiceId}")
    public BookingConfirmationDto getBookingConfirmation(@PathVariable int invoiceId) {
        return bookingService.getBookingConfirmation(invoiceId);
    }


}
