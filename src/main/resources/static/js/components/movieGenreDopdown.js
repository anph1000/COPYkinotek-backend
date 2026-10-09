
const genreSelect = document.getElementById("genre");
const genres = ["action", "adventure", "horror"] //harcoded for MVP

//fill dropdown with objects of genres fetched from api
function fillDropdownGenres(item) {
    const element = document.createElement("option")
    element.textContent = item
    element.value = item
    genreSelect.appendChild(element)
}

function populateGenreDropDown() {
    genres.forEach(fillDropdownGenres)
}
populateGenreDropDown()

//set genre to selected option

let movieGenre = ""

function setMovieGenre() {
    const selIndex = genreSelect.selectedIndex
    const selOption = genreSelect.options[selIndex]
    const selGenre = selOption.innerText
    movieGenre = selGenre
    console.log(movieGenre)
}

export {setMovieGenre}
export {populateGenreDropDown}