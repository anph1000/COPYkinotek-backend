import { fetchMovie, fetchUpcomingShowings } from "./api/movieApi.js";
import { renderShowings } from "./components/showingList.js";

const movieId = new URLSearchParams(location.search).get("movie");
const status = document.querySelector("#status");

init();

async function init() {
    try {
        const [movie, showings] = await Promise.all([
            fetchMovie(movieId),
            fetchUpcomingShowings(movieId),
        ]);
        renderMovieInfo(movie);
        renderShowings(document.querySelector("#showing-list"), showings);
        status.textContent = showings.length ? "" : "Ingen kommende forestillinger.";
    } catch {
        status.textContent = "Kunne ikke hente filmen. Prøv igen senere.";
    }
}

function renderMovieInfo(movie) {
    document.title = `${movie.movieName} – Kinotek`;
    document.querySelector("#movie-title").textContent = movie.movieName;
    document.querySelector("#movie-meta").textContent =
        `${movie.ageRating} · ${movie.duration} min · ${movie.genres.join(", ")}`;
    document.querySelector("#movie-description").textContent = movie.description;
}