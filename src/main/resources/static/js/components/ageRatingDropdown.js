// fylder aldersgrænse-dropdown med data fra /api/movies/age-ratings
export function populateAgeRatingDropdown(select, ageRatings) {
    select.replaceChildren(...ageRatings.map(createOption));
}

function createOption(ageRating) {
    const option = document.createElement("option");
    option.value = ageRating.id;
    option.textContent = ageRating.ageRating;
    return option;
}

export function selectedAgeRatingId(select) {
    return Number(select.value);
}
