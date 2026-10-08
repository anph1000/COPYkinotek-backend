import { fetchMovies } from "./api/movieApi.js";
import { renderMovieList } from "./components/movieListing.js";

const list = document.querySelector("#movie-list");
const status = document.querySelector("#status");

init();

async function init() {
    try {
        const movies = await fetchMovies();
        renderMovieList(list, movies);
        status.textContent = movies.length ? "" : "Ingen film på programmet lige nu.";
    } catch {
        status.textContent = "Kunne ikke hente film. Prøv igen senere.";
    }
}
