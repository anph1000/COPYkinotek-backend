import { fetchSeatMap, postBooking } from "./api/showingApi.js";
import { renderSeatMap } from "./components/seatPicker.js";
import { renderNav } from "./components/nav.js";
renderNav();

const form = document.querySelector("#seat-form");
const showingId = new URLSearchParams(location.search).get("showing");

init();

async function init() {
    if (!showingId) {
        showMessage("Ingen forestilling valgt.");
        return;
    }
    try {
        renderPage(await fetchSeatMap(showingId));
    } catch {
        showMessage("Kunne ikke hente sædekortet. Prøv igen senere.");
    }
}

function renderPage(seatMap) {
    document.querySelector("#movie-title").textContent = seatMap.movieName;
    renderSeatMap(document.querySelector("#seat-map"), seatMap);
    form.addEventListener("change", updateSummary);
    form.addEventListener("submit", handleSubmit);
}

async function refreshSeatMap() {
    try {
        renderSeatMap(document.querySelector("#seat-map"), await fetchSeatMap(showingId));
    } catch {
        showMessage("Kunne ikke opdatere sædekortet.");
    }
}

function createBookingRequest() {
    const formData = new FormData(form);
    return {
        showingId: Number(showingId),
        seatIds: formData.getAll("seat").map(Number),
        email: formData.get("email").trim(),
    };
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
    const button = event.submitter;
    const request = createBookingRequest();

    if (request.seatIds.length === 0) {
        showMessage("Vælg mindst ét sæde");
        return;
    }

    button.disabled = true;
    try {
        const confirmation = await postBooking(request);
        renderConfirmation(confirmation);
    } catch (err) {
        showMessage(err.message);
        button.disabled = false;
        // sædet kan være taget af en anden imens → hent sædekortet igen
        await fetchSeatMap(showingId);
    }
}

// TODO: Nedenstående message og confirmation er lidt hacked. Lad os erstatte dette med ægte html navigation.

function showMessage(text) {
    document.querySelector("#selection-summary").value = text;
}

function renderConfirmation(c) {
    const section = document.createElement("section");
    section.append(
        createElement("h2", "Tak for din booking!"),
        createElement("p", `Ordrenummer: ${c.invoiceId}`),
        createElement("p", `${c.movieName} · ${c.auditoriumName}`),
        createElement("p", formatShowingTime(c.showingDateTime)),
        createElement("p", `Sæder: ${formatSeats(c.bookedSeats)}`)
    );
    form.replaceWith(section);
}

function createElement(tag, text) {
    const element = document.createElement(tag);
    element.textContent = text;
    return element;
}

function formatSeats(seats) {
    return seats.map(s => `${s.seatRowLetter}${s.seatNumber}`).join(", ");
}

function formatShowingTime(dateTime) {
    return new Date(dateTime).toLocaleString("da-DK", { dateStyle: "full", timeStyle: "short" });
}