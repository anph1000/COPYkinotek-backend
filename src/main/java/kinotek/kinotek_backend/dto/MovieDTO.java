package kinotek.kinotek_backend.dto;

import java.util.List;

// ageRatingId og genreIds bruges når en film oprettes/opdateres.
// ageRating og genres er navnene til visning og sendes kun med i svaret.
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
        List<String> genres) {

}
