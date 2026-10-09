package kinotek.kinotek_backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookingConfirmationDto (
    int invoiceId,
    int showingId,
    String auditoriumName,
    String movieName,
    LocalDateTime showingDateTime,
    List<SeatStatusDto> bookedSeats,
    LocalDateTime purchaseTime

)    {}
