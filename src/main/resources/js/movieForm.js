if (sessionStorage.getItem("role") !== "Admin") location.href = "login.html";

import { fetchGenres, fetchAgeRatings, postMovie } from "./api/movieApi.js";
import { fillSelect } from "./components/formSelect.js";
import { renderNav } from "./components/nav.js";
renderNav();

const form = document.querySelector("#formMovie");
const genreSelect = document.querySelector("#genre");
const ageRatingSelect = document.querySelector("#ageRating");
const status = document.querySelector("#status");

init();

async function init() {
 try {
  const [genres, ageRatings] = await Promise.all([fetchGenres(), fetchAgeRatings()]);
  fillSelect(genreSelect, genres, "id", "genreName");
  fillSelect(ageRatingSelect, ageRatings, "id", "ageRating");
  form.addEventListener("submit", handleSubmit);
 } catch {
  showStatus("Kunne ikke hente genrer og aldersgrænser.");
 }
}

function createMovie() {
 const data = new FormData(form);
 return {
  movieName: data.get("title").trim(),
  description: data.get("description").trim(),
  duration: Number(data.get("duration")),
  imdbRef: data.get("imdbRef").trim(),
  imageRef: data.get("imageRef").trim() || null,
  ageRatingId: Number(data.get("ageRating")),
  genreIds: data.getAll("genre").map(Number),
 };
}

async function handleSubmit(event) {
 event.preventDefault(); // vi overskriver default opførsel for htmls <submit>
 try {
  const saved = await postMovie(createMovie()); // vi bruger vores POST i stedet
  showStatus(`Filmen "${saved.movieName}" er oprettet.`);
  form.reset();
 } catch (err) { // generic error
  showStatus(err.message);
 }
}

function showStatus(text) {
 status.textContent = text;
}