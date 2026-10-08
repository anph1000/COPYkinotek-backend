// fylder genre-listen (multiple select) med data fra /api/movies/genres
export function populateGenreDropDown(select, genres) {
    const sorted = [...genres].sort((a, b) => a.genreName.localeCompare(b.genreName, "da"));
    select.replaceChildren(...sorted.map(createOption));
}

function createOption(genre) {
    const option = document.createElement("option");
    option.value = genre.id;
    option.textContent = genre.genreName;
    return option;
}

export function selectedGenreIds(select) {
    return [...select.selectedOptions].map(option => Number(option.value));
}

export function selectGenres(select, genreIds) {
    [...select.options].forEach(option => {
        option.selected = genreIds.includes(Number(option.value));
    });
}
