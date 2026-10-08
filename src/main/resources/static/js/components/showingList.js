import { groupByDate, formatDay, formatTime } from "../utils/dateFormat.js";

export function renderShowings(container, showings) {
    const days = [...groupByDate(showings)];
    container.replaceChildren(...days.map(([date, list]) => createDay(date, list)));
}

function createDay(date, showings) {
    const section = document.createElement("section");
    section.append(createDayHeading(date), createShowingList(showings));
    return section;
}

function createDayHeading(date) {
    const heading = document.createElement("h3");
    const time = document.createElement("time");
    time.dateTime = date;
    time.textContent = formatDay(date);
    heading.append(time);
    return heading;
}

function createShowingList(showings) {
    const list = document.createElement("ul");
    list.append(...showings.map(createShowingItem));
    return list;
}

function createShowingItem(showing) {
    const item = document.createElement("li");
    item.append(createShowingLink(showing));
    return item;
}

function createShowingLink(showing) {
    const link = document.createElement("a");
    link.href = `booking.html?showing=${showing.id}`;
    link.append(createTime(showing), ` – ${showing.auditoriumName}`);
    return link;
}

function createTime(showing) {
    const time = document.createElement("time");
    time.dateTime = showing.dateTime;
    time.textContent = formatTime(showing.dateTime);
    return time;
}