export function renderMovieList(container, movies) {
    container.replaceChildren(...movies.map(createMovieItem));
}


function createMovieItem(movie) {
    const item = document.createElement("li");
    item.append(createMovieCard(movie));
    return item;
}

function createMovieCard(movie) {
    const article = document.createElement("article");
    article.append(createMovieLink(movie), createMeta(movie));
    return article
}

// linker til filmens side
function createMovieLink(movie) {
    const link = document.createElement("a");
    link.href = `pages/movie.html?movie=${movie.id}`;
    if (movie.imageRef) link.append(createPoster(movie));
    link.append(createTitle(movie));
    return link;
}

function createPoster(movie) {
    const img = document.createElement("img")
    img.src = movie.imageRef;
    img.alt = ""; //alt text, optional
    img.loading = "lazy"; //elementet indlæses kun hvis det vises / er nødvendigt
    return img;
}

function createTitle(movie) {
    const title = document.createElement("h2");
    title.textContent = movie.movieName;
    return title;
}


function createMeta(movie) {
    const meta = document.createElement("p");
    meta.textContent = `${movie.ageRating} · ${movie.duration} min · ${movie.genres.join(", ")}`;
    return meta;
}