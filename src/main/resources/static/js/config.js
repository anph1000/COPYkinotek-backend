const isLocalDevServer = location.hostname === "localhost" && location.port !== "";
export const API_BASE_URL = isLocalDevServer ? "http://localhost:8080" : "";