import { API_BASE_URL } from "../config.js";

export const fetchNowPlaying = () => getJson("/api/movies/now-playing");
export const fetchMovie = id => getJson(`/api/movies/${id}`);
export const fetchUpcomingShowings = movieId => getJson(`/api/showings/${movieId}`);

async function getJson(path) {
    const res = await fetch(`${API_BASE_URL}${path}`);
    if (!res.ok) throw new Error(`${res.status} ${path}`);
    return res.json();
}