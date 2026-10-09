export function renderSeatMap(container, seatMap) {
    container.replaceChildren(...seatMap.rows.map(createRow));
}

function createRow(row) {
    const fieldset = document.createElement("fieldset");
    fieldset.append(createLegend(row.rowLetter));
    fieldset.append(...row.seats.map(seat => createSeat(row.rowLetter, seat)));
    return fieldset;
}

function createLegend(rowLetter) {
    const legend = document.createElement("legend");
    legend.className = "visually-hidden";
    legend.textContent = `Række ${rowLetter}`;
    return legend;
}

function createSeat(rowLetter, seat) {
    const wrapper = document.createElement("span");
    wrapper.className = "seat";
    wrapper.append(createInput(seat), createLabel(rowLetter, seat));
    return wrapper;
}

function createInput(seat) {
    const input = document.createElement("input");
    input.type = "checkbox";
    input.name = "seat";
    input.value = seat.seatId;
    input.id = `seat-${seat.seatId}`;
    input.disabled = seat.booked;

    input.dataset.accessible = seat.accessible;
    return input;
}

function createLabel(rowLetter, seat) {
    const label = document.createElement("label");
    label.htmlFor = `seat-${seat.seatId}`;
    label.textContent = seat.seatNumber;
    label.title = `${rowLetter}${seat.seatNumber}`;
    return label;
}