import { http } from "./http";

const PIZZAS_PATH = "api/pizzas";
const DRINKS_PATH = "api/drinks";

export const catalogApi = {
    pizzas: (onlyAvailable = true) =>
        http.get(`${PIZZAS_PATH}?withVariants=false&all=${onlyAvailable ? "false" : "true"}`),
    drinks: (onlyAvailable = true) =>
        http.get(`${DRINKS_PATH}?availableOnly=${onlyAvailable ? "true" : "false"}`),
};
