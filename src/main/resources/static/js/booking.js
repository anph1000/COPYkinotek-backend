import { fetchSeatMap, postBooking } from "./api/showingApi.js";
import { renderSeatMap } from "./components/seatPicker.js";

const form = document.querySelector("#seat-form");
const showingId = new URLSearchParams(location.search).get("showing");

init();

async function init() {
    const seatMap = await fetchSeatMap(showingId);
    renderSeatMap(document.querySelector("#seat-map"), seatMap);
    form.addEventListener("change", updateSummary);
    form.addEventListener("submit", handleSubmit);
}

function selectedSeatIds() {
    return new FormData(form).getAll("seat").map(Number);
}

function updateSummary() {
    const count = selectedSeatIds().length;
    document.querySelector("#selection-summary").value =
        count === 0 ? "No seats selected" : `${count} seat(s) selected`;
}

async function handleSubmit(event) {
    event.preventDefault();
    await postBooking(showingId, selectedSeatIds());
}