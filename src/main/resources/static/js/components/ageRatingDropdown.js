const ageRatingSelect = document.getElementById("ageRating");
const ageRatings = ["A", "7", "11", "15"] //hardcoded for MVP -- change to get from backend

//fill dropdown
function fillDdAgeRating(item) {
    const element = document.createElement("option")
    element.textContent = item
    element.value = item
    ageRatingSelect.appendChild(element)
}

function populateAgeRatingDropdown () {
    ageRatings.forEach(fillDdAgeRating)
}

populateAgeRatingDropdown()
export {populateAgeRatingDropdown}

//set age rating to selected option

let movieRating = ""

function setMovieRating() {
    const selIndex = ageRatingSelect.selectedIndex
    const selOption = ageRatingSelect.options[selIndex]
    const selAgeRating = selOption.innerText
    movieRating = selAgeRating
    console.log(movieRating)
}

export {setMovieRating}