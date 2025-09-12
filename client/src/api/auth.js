import { http, tokenStore } from "./http";

/**
 *  POST /auth/register  { fullname, username, password } -> { username, token }
 *  POST /auth/login     { username, password }           -> { username, token }
 *  GET  /users/me       -> User (requires Bearer)
 */
export const authApi = {
    async register(payload) {
        const res = await http.post("/auth/register", payload);
        if (res?.token) tokenStore.set(res.token);
        return res;
    },
    async login(payload) {
        const res = await http.post("/auth/login", payload);
        if (res?.token) tokenStore.set(res.token);
        return res;
    },
    me: () => http.get("/users/me"),
    logout: async () => { tokenStore.clear(); },
};
