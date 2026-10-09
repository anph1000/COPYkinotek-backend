import { fetchMovie, fetchUpcomingShowings } from "./api/movieApi.js";
import { groupByDate } from "./utils/dateFormat.js";
import { renderCalendar } from "./components/calendar.js";
import { renderNav } from "./components/nav.js";
renderNav();

const movieId = new URLSearchParams(location.search).get("movie");
const status = document.querySelector("#status");
const section = document.querySelector("#calendar-section");
const table = document.querySelector("#calendar");
const prevButton = document.querySelector("#prev-month");
const nextButton = document.querySelector("#next-month");

let showingsByDate;
let current;      // { år, md } vist lige nu (month is 0-11)
let first;        // første md for showings
let last;         //sidste md for showings

init();

async function init() {
    try {
        const [movie, showings] = await Promise.all([
            fetchMovie(movieId),
            fetchUpcomingShowings(movieId),
        ]);
        renderMovieInfo(movie);
        section.hidden = showings.length === 0;
        if (showings.length === 0) {
            status.textContent = "Ingen kommende forestillinger.";
            return;
        }
        setupCalendar(showings);
    } catch {
        status.textContent = "Kunne ikke hente filmen. Prøv igen senere.";
    }
}
//  grupperer showings efter dato
// starter på d. 1. i md -> next/back buttons
function setupCalendar(showings) {
    showings.sort((a, b) => a.dateTime.localeCompare(b.dateTime)); // ISO strings sorterer korrekt
    showingsByDate = groupByDate(showings);
    first = monthOf(showings[0].dateTime);
    last = monthOf(showings.at(-1).dateTime);
    current = first;
    prevButton.addEventListener("click", () => changeMonth(-1));
    nextButton.addEventListener("click", () => changeMonth(1));
    showMonth();
}
// render til når vi går til næste md
function changeMonth(step) {
    current = fromIndex(toIndex(current) + step);
    showMonth();
}
// render nuværende md
function showMonth() {
    renderCalendar(table, current.year, current.month, showingsByDate);
    // next / back knapper er deaktiveret hvis der ikke er showings på måneden før/efter
    // se hjælper nede under
    prevButton.disabled = toIndex(current) <= toIndex(first);
    nextButton.disabled = toIndex(current) >= toIndex(last);
}
// gør datetime læseligt
function monthOf(dateTime) {
    return { year: Number(dateTime.slice(0, 4)), month: Number(dateTime.slice(5, 7)) - 1 };
}
// konverterer måneder til tal vi kan regne på
// repræsenterer en dato som antal mdr siden år 0
function toIndex({ year, month }) {
    return year * 12 + month;
}
// vi går den anden vej -> fra antal år siden år 0 til hvilken måned og årstal vi snakker om
// https://blog.logrocket.com/mastering-modulo-operator-javascript/
function fromIndex(index) {
    return { year: Math.floor(index / 12), month: index % 12 };
}

function renderMovieInfo(movie) {
    document.title = `${movie.movieName} – Kinotek`;
    document.querySelector("#movie-title").textContent = movie.movieName;
    document.querySelector("#movie-meta").textContent =
        `${movie.ageRating} · ${movie.duration} min · ${movie.genres.join(", ")}`;
    document.querySelector("#movie-description").textContent = movie.description;

    if (movie.imageRef) {
        const poster = document.createElement("img");
        poster.src = movie.imageRef;
        poster.alt = `Plakat for ${movie.movieName}`;
        poster.className = "movie-poster";
        document.querySelector("article").prepend(poster);
    }
}