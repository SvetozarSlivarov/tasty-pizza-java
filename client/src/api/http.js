const API_BASE = "";

const TOKEN_KEY = "tp_token";
export const tokenStore = {
    get: () => localStorage.getItem(TOKEN_KEY),
    set: (t) => localStorage.setItem(TOKEN_KEY, t),
    clear: () => localStorage.removeItem(TOKEN_KEY),
};

async function request(path, { method = "GET", body, headers } = {}) {
    const token = tokenStore.get();

    const res = await fetch(`${API_BASE}${path}`, {
        method,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(headers || {}),
        },
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await res.text();
    let data = null;
    if (text) { try { data = JSON.parse(text); } catch { data = text; } }

    if (!res.ok) {
        const msg = (data && (data.message || data.error)) || res.statusText;
        const err = new Error(msg);
        err.status = res.status;
        err.data = data;
        throw err;
    }
    return data;
}

export const http = {
    get:  (p)       => request(p),
    post: (p, body) => request(p, { method: "POST", body }),
    put:  (p, body) => request(p, { method: "PUT", body }),
    del:  (p)       => request(p, { method: "DELETE" }),
};
