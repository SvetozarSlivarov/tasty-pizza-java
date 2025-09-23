import { http } from "./http";

const PIZZAS = "/api/pizzas";
const DRINKS = "/api/drinks";
const INGREDIENTS = "/api/ingredients";
const INGREDIENT_TYPES = "/api/ingredient-types";
const USERS = "/users";
const HEALTH = "/health";

export const adminApi = {
    // Health
    health: () => http.get(HEALTH),

    // Pizzas
    listPizzas: (withVariants = false, all = true) =>
        http.get(`${PIZZAS}?withVariants=${withVariants ? "true" : "false"}&all=${all ? "true" : "false"}`),
    getPizza: (id, withVariants = true) =>
        http.get(`/api/pizzas/${id}?withVariants=${withVariants ? "true" : "false"}`),

    createPizza: (payload) => http.post(PIZZAS, payload),
    updatePizza: (id, payload) => http.patch(`${PIZZAS}/${id}`, payload),
    deletePizza: (id) => http.del(`${PIZZAS}/${id}`),

    uploadPizzaImageBase64: (id, { filename, contentType, dataBase64 }) =>
        http.post(`${PIZZAS}/${id}/image`, { filename, contentType, dataBase64 }),

    // Drinks
    listDrinks: (availableOnly = false) =>
        http.get(`${DRINKS}?availableOnly=${availableOnly ? "true" : "false"}`),
    createDrink: (payload) => http.post(DRINKS, payload),
    updateDrink: (id, payload) => http.patch(`${DRINKS}/${id}`, payload),
    deleteDrink: (id) => http.del(`${DRINKS}/${id}`),

    // Ingredients
    listIngredients: () => http.get(INGREDIENTS),
    createIngredient: (payload) => http.post(INGREDIENTS, payload),
    updateIngredient: (id, payload) => http.patch(`${INGREDIENTS}/${id}`, payload),
    deleteIngredient: (id) => http.del(`${INGREDIENTS}/${id}`),

    // Ingredient types
    listIngredientTypes: () => http.get(INGREDIENT_TYPES),
    createIngredientType: (payload) => http.post(INGREDIENT_TYPES, payload),
    updateIngredientType: (id, payload) => http.patch(`${INGREDIENT_TYPES}/${id}`, payload),
    deleteIngredientType: (id) => http.del(`${INGREDIENT_TYPES}/${id}`),

    // Users (no list endpoint; role updates only)
    updateUserRoleById: (id, role) => http.put(`${USERS}/${id}/role`, { role }),
    updateUserRoleByUsername: (username, role) =>
        http.put(`${USERS}/by-username/${encodeURIComponent(username)}/role`, { role }),
};
