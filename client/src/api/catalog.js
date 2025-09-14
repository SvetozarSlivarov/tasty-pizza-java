import { http } from "./http";

const PIZZAS_PATH = "/api/pizzas";
const DRINKS_PATH = "/api/drinks";

export const catalogApi = {
    pizzas: (onlyAvailable = true, withVariants = false) =>
        http.get(
            `${PIZZAS_PATH}?withVariants=${withVariants ? "true" : "false"}&all=${onlyAvailable ? "false" : "true"}`
        ),
    drinks: (onlyAvailable = true) =>
        http.get(`${DRINKS_PATH}?availableOnly=${onlyAvailable ? "true" : "false"}`),
};

export const productApi = {
    pizza: (id, withVariants = true) =>
        http.get(`${PIZZAS_PATH}/${id}?withVariants=${withVariants ? "true" : "false"}`),

    pizzaIngredients: (id) => http.get(`${PIZZAS_PATH}/${id}/ingredients`),

    pizzaAllowedIngredients: (id) => http.get(`${PIZZAS_PATH}/${id}/allowed-ingredients`),

    drink: (id) => http.get(`${DRINKS_PATH}/${id}`),
};
