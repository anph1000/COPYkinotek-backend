package kinotek.kinotek_backend.dto;

public record SeatStatusDto (
        int seatId,
        int seatRowId,
        String seatRowLetter,
        int seatNumber,
        boolean accessible,
        boolean booked

) {
}
