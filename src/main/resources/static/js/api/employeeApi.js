import { API_BASE_URL } from "../config.js";

export async function login(name, password) {
    // afvent endpoint fra backend def. i employeerestcontroller
    const res = await fetch(`${API_BASE_URL}/api/employee/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, password }),
    });
    if (!res.ok) return false;
    // her gemmer vi vha. sessionstorage
    // ikke en cookie = vi gemmer direkte i browseren
    // lidt langhåret, læs. https://developer.mozilla.org/en-US/docs/Web/API/Window/sessionStorage
    const data = await res.json();
    sessionStorage.setItem("employee", data.name);
    sessionStorage.setItem("role", data.role);
    return true;

    // OBS - hver gang vi skal bruge en admin, læg den her ind:
    // CHECK = EMPLOYEE?
    // if (!sessionStorage.getItem("employee")) location.href = "login.html";
    // CHECK = ADMIN?
    // if (sessionStorage.getItem("role") !== "Admin") location.href = "login.html";
    // den siger at vi skal tjekke om browseren har gemt en employee. eller en role som er admin.
}