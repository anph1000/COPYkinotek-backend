export async function fetchSeatMap(showingId) {
    const [auditorium, bookedIds] = await Promise.all([
        getJson(`/api/showings/${showingId}/auditorium`),
        getJson(`/api/showings/${showingId}/booked-seats`),
    ]);
    const booked = new Set(bookedIds);
    auditorium.rows.forEach(row =>
        row.seats.forEach(seat => { seat.booked = booked.has(seat.id); }));
    return auditorium;
}

async function getJson(path) {
    const res = await fetch(`${API_BASE_URL}${path}`);
    return res.json();
}