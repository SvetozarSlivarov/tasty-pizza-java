# Tasty Pizza — Full‑Stack App (Java 21 + MySQL + React)

A complete pizza ordering demo with **customizable pizzas**, **drinks**, **shopping cart**, **orders**, **user accounts with JWT auth**, **role‑based admin**, and **image uploads to Cloudinary**.

This repo is split into two apps:

- `server/` — Java 21 HTTP server (JDK built‑in `HttpServer`) + MySQL 8, JWT, DAOs; schema bootstrapping and seed data included.
- `client/` — React app (CRA) that consumes the API and includes a customer UI and an admin panel.

---

## ✨ Features at a glance

- 🔐 **Auth & Roles** — JWT login/register; roles: `CUSTOMER` and `ADMIN` (seed admin is created).
- 🧾 **Catalog** — Pizzas (with variants & base ingredients) and Drinks.
- 🛒 **Cart & Orders** — Add pizzas/drinks, customize, place orders; order history.
- 🖼️ **Images** — Upload product images to **Cloudinary**.
- 🧰 **Admin** — Manage pizzas, ingredients, types, drinks, users; view orders.
- 🌐 **CORS** — Open CORS for local development.
- 🧱 **Zero‑framework server** — Clean controller/router layers over `com.sun.net.httpserver.HttpServer` with DAOs over MySQL.

---

## 🚀 Quick start

> Prerequisites: **Java 21**, **Maven 3.9+**, **Node.js 20+**, **MySQL 8.0+**, a **Cloudinary** account.

### 1) Configure the database

The server ships with a properties file at:
```text
/mnt/data/server_extracted/server/src/main/resources/db.properties
```

Default content (change the user/password for your local setup!):
```properties
db.url=jdbc:mysql://localhost:3306/tasty_pizza?allowPublicKeyRetrieval=true&useSSL=false

db.user=root
db.password=123456
```

On first run, the app will:
- create database **`tasty_pizza`** (if missing),
- create tables, and
- seed an **admin** user.

**Seed admin credentials** (change the password after first login):
```text
username: admin
password: admin123
```

### 2) Set environment variables

The server needs JWT and Cloudinary settings. You can export them, or create a `.env` (the app auto‑loads it from repo root or `./server`).

```bash
# Server
export PORT=8080
# base64-encoded secret; generate: `openssl rand -base64 32`
export JWT_SECRET="rZg9l5mVxqkz6j+QG3WkX1XzF9yR8m2cQ3ZrT5wY2pA="
export JWT_TTL=3600

# Cloudinary (required for image upload)
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
```

> Tip: You can also put the same variables into a `.env` file. The server will try to load it automatically.

### 3) Run the **server**

```bash
cd server
# Build and copy dependencies
mvn -q clean package dependency:copy-dependencies
# Run
java -cp target/classes:target/dependency/* com.example.App
# Server listens at:
# http://localhost:8080
```

> IntelliJ users: create an Application run configuration with `com.example.App` main class and the env vars above.

### 4) Run the **client**

The client is a React app created with CRA; it proxies API requests to `http://localhost:8080` via `package.json` "proxy".

```bash
cd client_extracted
npm install
npm start
# App opens on http://localhost:3000
```

Login with the **seed admin** to access `/admin` pages.

---

## 🧭 Project structure

```text
server/
  src/main/java/com/example/
    App.java                       # entrypoint: starts HttpServer
    config/                        # Beans wiring & ServerConfig (.env support)
    controller/                    # Auth, Users, Pizzas, Drinks, Cart, Orders, Admin...
    dao/ + dao/impl/ + dao/base/   # MySQL DAOs
    db/                            # DBConnection, SchemaBuilder, DatabaseInitializer, SeedData
    dto/                           # Request/response DTOs
    http/                          # Filters, helpers, CORS, logging
    model/                         # Entities + enums
    security/                      # JwtService + filter, password hashing (BCrypt)
    server/RouteRegistrar.java     # All routes mapped here
    service/                       # Business logic services
    storage/                       # Cloudinary storage abstraction
    utils/                         # JSON, cart janitor, mappers
  src/main/resources/db.properties # MySQL connection (edit locally)
  pom.xml

client_extracted/
  public/
  src/
    api/                           # HTTP client and endpoint wrappers
    components/                    # UI components (Navbar, etc.)
    context/                       # Auth/Cart contexts
    pages/                         # Customer + /admin pages
    styles/                        # CSS modules and global styles
    utils/                         # helpers (e.g., fileToBase64)
  package.json
```

---

## 🔌 API overview (selected endpoints)

Base URL: `http://localhost:8080`

### Auth
- `POST /auth/register` → `{ username, token }`
- `POST /auth/login` → `{ username, token }` (also ensures a cart and sets `cartId` cookie)

### Users
- `GET /users/me` — current user profile
- `PUT /users/me` — update profile (returns new token if username changes)
- `PATCH /users/{id}/role` — **ADMIN**: change role

### Pizzas
- `GET /api/pizzas?withVariants=true|false&all=true|false`
- `GET /api/pizzas/{id}?withVariants=true|false`
- `POST /api/pizzas` — **ADMIN**
- `PATCH /api/pizzas/{id}` — **ADMIN**
- `DELETE /api/pizzas/{id}` — **ADMIN**
- `POST /api/pizzas/{id}/image` — **ADMIN** upload image to Cloudinary
- `GET /api/pizzas/{id}/ingredients` — list base ingredients
- `POST /api/pizzas/{id}/ingredients` — **ADMIN** add ingredient
- `PATCH /api/pizzas/{id}/ingredients/{ingredientId}` — **ADMIN** update removable flag
- `DELETE /api/pizzas/{id}/ingredients/{ingredientId}` — **ADMIN** remove
- `GET /api/pizzas/{id}/allowed` — list allowed extra ingredients
- `POST /api/pizzas/{id}/allowed` — **ADMIN** add allowed
- `DELETE /api/pizzas/{id}/allowed/{ingredientId}` — **ADMIN** remove

### Drinks
- `GET /api/drinks`
- `GET /api/drinks/{id}`
- `POST /api/drinks` — **ADMIN**
- `PATCH /api/drinks/{id}` — **ADMIN**
- `DELETE /api/drinks/{id}` — **ADMIN**
- `POST /api/drinks/{id}/image`

### Ingredients & Types
- `GET /api/ingredients` / `GET /api/ingredient-types`
- `POST/PATCH/DELETE` on both — **ADMIN**

### Cart & Orders
- `GET /api/cart` — current cart view
- `POST /api/cart/pizzas` — add pizza with customizations
- `POST /api/cart/drinks` — add drink
- `PATCH /api/cart/items/{id}` — update qty/customizations
- `DELETE /api/cart/items/{id}` — remove item
- `POST /api/orders` — place order from cart
- **Admin Orders**: `/api/admin/orders`, `/api/admin/orders/{id}` (list/details/status)

> All endpoints requiring auth expect `Authorization: Bearer <jwt>` and also use a `cartId` cookie for cart continuity.

---

## 🧪 Running in development

- The server enables **CORS** for `*` and handles `OPTIONS` preflights.
- The client stores the token in `localStorage` under `tp_token` and sends it on requests.
- Proxy is set in client `package.json`: `"proxy": "http://localhost:8080"`.

---

## 🛡️ Security notes

- **Change DB credentials** in `db.properties` before committing or deploying.
- **Rotate JWT secret** to a strong random value (base64).
- Update the seeded **admin password** ASAP.
- Cloudinary keys are required for uploads; never commit real secrets.
- Consider adding rate limiting and validation on DTOs for production.


---

---

## 📦 Tech stack

- **Server:** Java 21, Maven, MySQL 8, JJWT, Jackson, BCrypt, java-dotenv, Cloudinary SDK
- **Client:** React (CRA), React Router, react-hot-toast, react-icons

---

## 🗺️ Roadmap ideas

- Dockerfiles + docker‑compose (MySQL + server + client)
- Pagination on catalog endpoints
- E2E tests
- Image transformations (Cloudinary presets)

---
