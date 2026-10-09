import { formatDay, formatTime } from "../utils/dateFormat.js";

const LOCALE = "da-DK";
const WEEKDAYS = [["Man", "mandag"], ["Tir", "tirsdag"], ["Ons", "onsdag"], ["Tor", "torsdag"],
    ["Fre", "fredag"], ["Lør", "lørdag"], ["Søn", "søndag"]];

export function renderCalendar(table, year, month, showingsByDate) {
    table.replaceChildren(
        createCaption(year, month),
        createHead(),
        createBody(year, month, showingsByDate)
    );
}

// bygger header så den ser forholdsvis pæn ud -> ex "oktober 2026"
function createCaption(year, month) {
    const caption = document.createElement("caption");
    caption.setAttribute("aria-live", "polite");
    caption.textContent = new Date(year, month, 1)
        .toLocaleDateString(LOCALE, { month: "long", year: "numeric" });
    return caption;
}

// man -> søn header rækker
function createHead() {
    const head = document.createElement("thead");
    const row = document.createElement("tr");
    row.append(...WEEKDAYS.map(([short, full]) => createWeekdayHeader(short, full)));
    head.append(row);
    return head;
}

// formatering af header -> abbr = skærmlæser kan læse hele ugen
function createWeekdayHeader(short, full) {
    const th = document.createElement("th");
    th.scope = "col";
    th.abbr = full; // abbr = til skærmlæsere
    th.textContent = short;
    return th;
}

// 1 række pr uge
function createBody(year, month, showingsByDate) {
    const body = document.createElement("tbody");
    const rows = createWeeks(year, month).map(week => createWeekRow(week, year, month, showingsByDate));
    body.append(...rows);
    return body;
}

// kalender math
function createWeeks(year, month) {
    // vi sætter mandag til at altid ville være 0 -> så vi finder den første hverdag på måneden.
    //ex: hvis den første hverdag er en torsdag = (4+6)%7 = 3 -> der skal være 3 tomme celler før torsdag
    const offset = (new Date(year, month, 1).getDay() + 6) % 7; // uge starter altid mandag
    //0 her er = dagen før d. 1. da format er = year, month, day.
    // så i stedet for day = 0. 0 er ikke en dato = defaulter til sidste dag før måneden
    // derfor ligger vi 1 måned oveni for at få den vi gerne vil have.
    // ex: vi vil have fat i januar. vi springer til februar, får "dag 0" -> tilbage til 31. jan
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const cells = [
        ...Array(offset).fill(null),
        ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
    ];
    // null her = hver uge har kun 7 celler.
    // hvis der mangler en dag (fx vi har 36 dage):
    // js putter en null celle i indtil vi rammer næste i 7 tabellen, i eksemplet vil det være indtil der er 42
    while (cells.length % 7 !== 0) cells.push(null);
    // vi tager kun 7 ad gangen
    return Array.from({ length: cells.length / 7 }, (_, w) => cells.slice(w * 7, w * 7 + 7));
}

// hver uge = tr
function createWeekRow(week, year, month, showingsByDate) {
    const row = document.createElement("tr");
    row.append(...week.map(day => createDayCell(day, year, month, showingsByDate)));
    return row;
}

// hver dag = td
function createDayCell(day, year, month, showingsByDate) {
    const cell = document.createElement("td");
    if (day === null) return cell;
    // hver dag får showings i popover -> viser showings for den dag
    const dateKey = toDateKey(year, month, day);
    const showings = showingsByDate.get(dateKey);
    if (showings) {
        cell.append(createDayButton(day, dateKey, showings.length), createPopover(dateKey, showings));
    } else {
        cell.append(createDayNumber(day, dateKey));
    }
    return cell;
}
// time element for dagen uden showings (til visning)
function createDayNumber(day, dateKey) {
    const time = document.createElement("time");
    time.dateTime = dateKey;
    time.textContent = day;
    return time;
}

// vores popover til hver dag -> hver dag er en button med popover attribute
function createDayButton(day, dateKey, count) {
    const button = document.createElement("button");
    button.type = "button";
    button.setAttribute("popovertarget", `day-${dateKey}`);
    // læses som fx: "torsdag 9. oktober, 3 forestillinger"
    button.setAttribute("aria-label", `${formatDay(dateKey)}, ${count} forestillinger`);
    button.textContent = day;
    return button;
}

// populater vores popover med showings -> section element for den dag
function createPopover(dateKey, showings) {
    const section = document.createElement("section");
    section.id = `day-${dateKey}`;
    section.setAttribute("popover", "");
    section.setAttribute("aria-labelledby", `day-${dateKey}-title`);
    section.append(createPopoverHeading(dateKey), createTimeList(showings));
    return section;
}

// header for popover
function createPopoverHeading(dateKey) {
    const heading = document.createElement("h3");
    heading.id = `day-${dateKey}-title`;
    heading.textContent = formatDay(dateKey);
    return heading;
}
// tiderne er en liste
function createTimeList(showings) {
    const list = document.createElement("ul");
    list.append(...showings.map(createTimeItem));
    return list;
}
// link til showing for den tid -> hentes fra hjælper
function createTimeItem(showing) {
    const item = document.createElement("li");
    item.append(createShowingLink(showing));
    return item;
}
// link til showing i en anchor
function createShowingLink(showing) {
    const link = document.createElement("a");
    link.href = `booking.html?showing=${showing.id}`;
    link.append(createTime(showing), ` – ${showing.auditorium}`);
    return link;
}
// laver datetime elementet til html
function createTime(showing) {
    const time = document.createElement("time");
    time.dateTime = showing.dateTime;
    time.textContent = formatTime(showing.dateTime);
    return time;
}
// formattering af dato
function toDateKey(year, month, day) {
    const mm = String(month + 1).padStart(2, "0");
    const dd = String(day).padStart(2, "0");
    return `${year}-${mm}-${dd}`;
}