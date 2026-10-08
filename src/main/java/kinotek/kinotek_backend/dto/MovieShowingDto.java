package kinotek.kinotek_backend.dto;

import java.time.LocalDateTime;

// En kommende forestilling som den vises på filmens side
public record MovieShowingDto(
        int id,
        LocalDateTime dateTime,
        String auditoriumName) {
}
