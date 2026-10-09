package kinotek.kinotek_backend.dto;

import java.util.Set;

public record BookingRequestDto (
    int showingId,
    Set<Integer> seatIds,
    String email
) {}
