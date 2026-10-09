package kinotek.kinotek_backend.service;


import kinotek.kinotek_backend.dto.BookingConfirmationDto;
import kinotek.kinotek_backend.dto.BookingRequestDto;
import kinotek.kinotek_backend.dto.SeatMapDto;
import kinotek.kinotek_backend.model.cinema.Booking;
import kinotek.kinotek_backend.model.user.Customer;

import java.util.List;
import java.util.Set;


public interface BookingService {

    List<Booking> getBookings();

    public Set<Integer> bookedSeatIdsByShowingId(int showingId);

    public int createBookings(BookingRequestDto bookingRequestDto);

    public BookingConfirmationDto getBookingConfirmation(int invoiceId);

    public SeatMapDto getSeatMap(int showingId);

}
