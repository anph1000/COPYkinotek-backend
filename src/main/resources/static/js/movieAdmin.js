import { fetchMovies, deleteMovie } from "./api/movieApi.js";
import { movieMeta } from "./components/movieListing.js";

const rows = document.querySelector("#movie-rows");
const status = document.querySelector("#status");

init();

async function init() {
    try {
        const movies = await fetchMovies();
        movies.sort((a, b) => a.movieName.localeCompare(b.movieName, "da"));
        rows.replaceChildren(...movies.map(createRow));
        status.textContent = movies.length ? "" : "Der er ingen film endnu.";
    } catch (error) {
        status.textContent = `Kunne ikke hente film: ${error.message}`;
    }
}

function createRow(movie) {
    const row = document.createElement("tr");
    row.append(
        createCell(createLink(movie.movieName, `movie.html?movie=${movie.id}`)),
        createCell(movieMeta(movie)),
        createCell(createLink("Rediger", `movie-form.html?movie=${movie.id}`), createDeleteButton(movie, row)),
    );
    return row;
}

function createCell(...content) {
    const cell = document.createElement("td");
    cell.append(...content);
    return cell;
}

function createLink(text, href) {
    const link = document.createElement("a");
    link.href = href;
    link.textContent = text;
    return link;
}

function createDeleteButton(movie, row) {
    const button = document.createElement("button");
    button.type = "button";
    button.textContent = "Slet";
    button.addEventListener("click", () => handleDelete(movie, row));
    return button;
}

async function handleDelete(movie, row) {
    if (!confirm(`Slet "${movie.movieName}"?`)) return;
    try {
        await deleteMovie(movie.id);
        row.remove();
        status.textContent = `"${movie.movieName}" er slettet.`;
    } catch (error) {
        status.textContent = `Kunne ikke slette "${movie.movieName}": ${error.message}`;
    }
}
