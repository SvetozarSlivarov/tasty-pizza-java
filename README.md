# Tasty Pizza — Java + React

Minimal full-stack app: **Java HTTP server** + **React** frontend.

---

## Tech

* **Backend:** Java (com.sun.net.httpserver), JWT auth, JSON APIs
* **Frontend:** React 19 (CRA), React Router 7

---

## Folder structure

### Frontend – `Client/`

```
Client/
├─ public/                 # index.html, favicon, manifest
├─ src/
│  ├─ api/
│  │  ├─ http.js          # fetch wrapper (Bearer token, error handling)
│  │  └─ auth.js          # login, register, me, logout
│  ├─ components/
│  │  ├─ Navbar.jsx       # dynamic: Sign in / Profile / Admin / Sign out
│  │  └─ Footer.jsx
│  ├─ context/
│  │  └─ AuthContext.jsx  # JWT in localStorage, /users/me bootstrap
│  ├─ pages/
│  │  ├─ Home.jsx   ├─ Menu.jsx   ├─ Cart.jsx
│  │  ├─ Login.jsx  ├─ Register.jsx
│  │  ├─ Profile.jsx       # placeholder
│  │  └─ Admin.jsx         # placeholder
│  └─ styles/              # navbar.css, footer.css, login.css, register.css, etc.
└─ package.json            # includes "proxy": "http://localhost:8080"
```

### Backend – `Server/src/main/java/com/example/`

```
src/main/java/com/example/
├─ config/      # app config, constants
├─ controller/  # request handlers (auth, users, domain)
├─ dao/         # data access (files/DB)
├─ db/          # DB init/seed/utils
├─ dto/         # request/response models
├─ exception/   # custom exceptions + mappers
├─ http/        # routing, filters, middleware
├─ model/       # core entities (User, Pizza, ...)
├─ security/    # JwtService, auth helpers
├─ server/      # HTTP server bootstrap, route registrar
├─ service/     # business logic (users, pizzas, cart)
├─ utils/       # helpers (JSON, validation, etc.)
└─ App.java     # entry point (starts server on :8080)

src/main/resources/
└─ ...          # seeds/config (if any)
```

---

## Run locally

### 1) Backend (port **8080**)

* Open Java project in IDE and run **`App`** (main method).
* Server listens at `http://localhost:8080`.

### 2) Frontend (port **3000**)

```bash
cd Client
npm install
# ensure package.json has:  "proxy": "http://localhost:8080"
npm start
```

CRA dev server proxies `/auth`, `/users`, `/api` to `:8080`.

---

## Auth (JWT)

* `POST /auth/register` → `{ fullname, username, password }` → `{ username, token }`
* `POST /auth/login`    → `{ username, password }`         → `{ username, token }`
* `GET  /users/me`      → current user (requires `Authorization: Bearer <token>`)

Frontend stores the token in **localStorage** and sends it on every request.
On boot, if **no token**, frontend **does not** call `/users/me` (prevents phantom users).

---

## Frontend routes

* `/` Home • `/menu` • `/cart`
* `/login` • `/register`
* `/profile` (placeholder)
* `/admin`  (shown in navbar if role = ADMIN; backend still enforces auth)

---

## Notes

* Security is enforced **on the backend** (validate JWT & roles for protected endpoints).
* If not using CRA proxy, set `API_BASE` in `src/api/http.js` to `http://localhost:8080`.
* Logout is client-side (clear token); no backend endpoint required for now.

---

## Roadmap

* Wire Menu/Cart to `/api/*`
* Profile edit
* Admin guard + pages
* Orders & checkout
