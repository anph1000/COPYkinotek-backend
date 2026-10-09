package kinotek.kinotek_backend.dto;

import java.util.List;

public record MovieDTO (
        Integer id,
        String movieName,
        int duration,
        String description,
        String imdbRef,
        String imageRef,
        int ageRatingId,
        List<Integer> genreIds,
        String ageRating,
        List<String> genres
        ) {

}
