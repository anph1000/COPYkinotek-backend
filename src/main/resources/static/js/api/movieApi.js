import { API_BASE_URL } from "../config.js";

export const fetchMovies = () => request("/api/movies");
export const fetchMovie = id => request(`/api/movies/${id}`);
export const fetchUpcomingShowings = movieId => request(`/api/movies/${movieId}/showings`);
export const fetchGenres = () => request("/api/movies/genres");
export const fetchAgeRatings = () => request("/api/movies/age-ratings");

export const createMovie = movie => request("/api/movies", "POST", movie);
export const updateMovie = (id, movie) => request(`/api/movies/${id}`, "PUT", movie);
export const deleteMovie = id => request(`/api/movies/${id}`, "DELETE");

async function request(path, method = "GET", body) {
    const options = { method };
    if (body !== undefined) {
        options.headers = { "Content-Type": "application/json" };
        options.body = JSON.stringify(body);
    }
    const res = await fetch(`${API_BASE_URL}${path}`, options);
    if (!res.ok) throw new Error(await errorMessage(res));
    return res.status === 204 ? null : res.json();
}

// backend sender fejlbeskeden i "message" (server.error.include-message=always)
async function errorMessage(res) {
    try {
        const error = await res.json();
        if (error.message) return error.message;
    } catch {
        // svaret var ikke JSON
    }
    return `${res.status} ${res.statusText}`;
}
