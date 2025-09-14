import { http } from "./http";

const PIZZAS = "/api/pizzas";
const DRINKS = "/api/drinks";

export const catalogApi = {
    pizzas: (onlyAvailable = true, withVariants = false) =>
        http.get(`${PIZZAS}?withVariants=${withVariants ? "true" : "false"}&all=${onlyAvailable ? "false" : "true"}`),
    drinks: (onlyAvailable = true) =>
        http.get(`${DRINKS}?availableOnly=${onlyAvailable ? "true" : "false"}`),
};

export const productApi = {
    pizza: (id, withVariants = true) =>
        http.get(`${PIZZAS}/${id}?withVariants=${withVariants ? "true" : "false"}`),
    pizzaIngredients:        (id) => http.get(`${PIZZAS}/${id}/ingredients`),
    pizzaAllowedIngredients: (id) => http.get(`${PIZZAS}/${id}/allowed-ingredients`),
    drink: (id) => http.get(`${DRINKS}/${id}`),
};
