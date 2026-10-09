import { API_BASE_URL } from "../config.js";

export async function fetchSeatMap(showingId) {
    const seatMap = await getJson(`/api/showing/${showingId}/seat-map`)
    return { ...seatMap, rows: groupSeatsByRow(seatMap.seats)}
}

export async function postBooking(bookingRequest) {
    const res = await fetch(`${API_BASE_URL}/api/bookings/create-booking-request`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingRequest),
    });
    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw new Error(error.message ?? `Booking fejlede: ${res.status}`);
    }
    return res.json();
}

function groupSeatsByRow(seats) {
    const rows = new Map();
    for (const seat of seats) {
        if (!rows.has(seat.seatRowId)) {
            rows.set(seat.seatRowId, { rowLetter: seat.seatRowLetter, seats: [] });
        }
        rows.get(seat.seatRowId).seats.push(seat);
    }
    return [...rows.values()];
}

async function getJson(path) {
    const res = await fetch(`${API_BASE_URL}${path}`);
    if (!res.ok) throw new Error(`GET ${path} fejlede: ${res.status}`);
    return res.json();
}