import { fetchMovie, fetchGenres, fetchAgeRatings, createMovie, updateMovie } from "./api/movieApi.js";
import { populateGenreDropDown, selectedGenreIds, selectGenres } from "./components/movieGenreDopdown.js";
import { populateAgeRatingDropdown, selectedAgeRatingId } from "./components/ageRatingDropdown.js";

// movie-form.html opretter en ny film, movie-form.html?movie=3 redigerer film 3
const movieId = new URLSearchParams(location.search).get("movie");

const form = document.querySelector("#movie-form");
const status = document.querySelector("#status");
const fields = {
    title: document.querySelector("#title"),
    description: document.querySelector("#description"),
    duration: document.querySelector("#duration"),
    genre: document.querySelector("#genre"),
    ageRating: document.querySelector("#ageRating"),
    imageRef: document.querySelector("#imageRef"),
    imdbRef: document.querySelector("#imdbRef"),
};

init();

async function init() {
    try {
        const [genres, ageRatings] = await Promise.all([fetchGenres(), fetchAgeRatings()]);
        populateGenreDropDown(fields.genre, genres);
        populateAgeRatingDropdown(fields.ageRating, ageRatings);
        if (movieId) fillForm(await fetchMovie(movieId));
        form.addEventListener("submit", handleSubmit);
    } catch (error) {
        status.textContent = `Kunne ikke hente data: ${error.message}`;
    }
}

function fillForm(movie) {
    const heading = `Rediger ${movie.movieName}`;
    document.title = `${heading} – Kinotek`;
    document.querySelector("#form-heading").textContent = heading;
    document.querySelector("#btn-submit").textContent = "Gem ændringer";

    fields.title.value = movie.movieName;
    fields.description.value = movie.description ?? "";
    fields.duration.value = movie.duration;
    fields.ageRating.value = movie.ageRatingId;
    fields.imageRef.value = movie.imageRef ?? "";
    fields.imdbRef.value = movie.imdbRef ?? "";
    selectGenres(fields.genre, movie.genreIds);
}

function readForm() {
    return {
        movieName: fields.title.value.trim(),
        description: fields.description.value.trim(),
        duration: Number(fields.duration.value),
        ageRatingId: selectedAgeRatingId(fields.ageRating),
        genreIds: selectedGenreIds(fields.genre),
        imageRef: fields.imageRef.value.trim() || null,
        imdbRef: fields.imdbRef.value.trim() || null,
    };
}

async function handleSubmit(event) {
    event.preventDefault();
    try {
        const movie = readForm();
        const saved = movieId ? await updateMovie(movieId, movie) : await createMovie(movie);
        location.href = `movie.html?movie=${saved.id}`;
    } catch (error) {
        status.textContent = `Filmen blev ikke gemt: ${error.message}`;
    }
}
