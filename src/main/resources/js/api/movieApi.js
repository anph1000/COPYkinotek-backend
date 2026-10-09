import { API_BASE_URL } from "../config.js";

export const fetchNowPlaying = () => getJson("/api/movies/now-playing");
export const fetchMovie = id => getJson(`/api/movies/${id}`);
export const fetchUpcomingShowings = movieId => getJson(`/api/showing/${movieId}`);
export const fetchGenres = () => getJson("/api/movies/genres");
export const fetchAgeRatings = () => getJson("/api/movies/age-ratings");

async function getJson(path) {
    const res = await fetch(`${API_BASE_URL}${path}`);
    if (!res.ok) throw new Error(`${res.status} ${path}`);
    return res.json();
}

export async function postMovie(createdMovie) {
    const res = await fetch(`${API_BASE_URL}/api/movies`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(createdMovie),
    });
    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw new Error(error.message ?? `Oprettelse af film fejlede: ${res.status}`);
    }
    return res.json();
}