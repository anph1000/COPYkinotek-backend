const LOCALE = "da-DK";


// stjålet online og justeret -> til at formattere vores datoer + tider pænt
export function groupByDate(showings) {
    const days = new Map();
    showings.forEach(showing => {
        const date = showing.dateTime.slice(0, 10);
        days.set(date, [...(days.get(date) ?? []), showing]);
    });
    return days;
}

export function formatDay(date) {
    return new Date(`${date}T00:00`).toLocaleDateString(LOCALE,
        { weekday: "long", day: "numeric", month: "long" });
}

export function formatTime(dateTime) {
    return new Date(dateTime).toLocaleTimeString(LOCALE, { hour: "2-digit", minute: "2-digit" });
}