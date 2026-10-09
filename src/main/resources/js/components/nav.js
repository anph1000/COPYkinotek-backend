const LINKS = [
    { label: "Film", path: "../../index.html", adminOnly: false },
    { label: "Opret film", path: "../../pages/movie-form.html", adminOnly: true },
];


export function renderNav() {
    document.body.prepend(createHeader());
}

function createHeader() {
    const header = document.createElement("header");
    header.className = "site-header";
    header.append(createNav());
    return header;
}

function createNav() {
    const nav = document.createElement("nav");
    nav.setAttribute("aria-label", "Hovednavigation");
    nav.append(createLogo(), createLinkList());
    return nav;
}

function createLogo() {
    const logo = document.createElement("a");
    logo.className = "logo";
    logo.href = resolve("../../index.html");
    logo.textContent = "Kinotek";
    return logo;
}

function createLinkList() {
    const list = document.createElement("ul");
    list.append(...visibleLinks().map(createLinkItem));
    return list;
}

function createLinkItem(link) {
    const item = document.createElement("li");
    item.append(createLink(link));
    return item;
}

function createLink(link) {
    const anchor = document.createElement("a");
    anchor.href = resolve(link.path);
    anchor.textContent = link.label;
    return anchor;
}

function visibleLinks() {
    return LINKS.filter(link => !link.adminOnly || isAdmin());
}

function isAdmin() {
    return sessionStorage.getItem("role") === "Admin";
}

function resolve(path) {
    return new URL(path, import.meta.url).href;
}
