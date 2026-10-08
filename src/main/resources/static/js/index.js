import { fetchNowPlaying } from "./api/movieApi.js";
import { renderMovieList } from "./components/movieList.js";

const list = document.querySelector("#movie-list");
const status = document.querySelector("#status");

init();

async function init() {
    try {
        const movies = await fetchNowPlaying();
        renderMovieList(list, movies);
        status.textContent = movies.length ? "" : "Ingen film på programmet lige nu.";
    } catch {
        status.textContent = "Kunne ikke hente film. Prøv igen senere.";
    }
}