import { http } from "./http";

export const cartApi = {
    get: () => http.get("/api/cart"),
    addDrink: ({ productId, quantity = 1, note = "" }) =>
        http.post("/api/cart/items/drink", { productId, quantity, note }),
    addPizza: ({ productId, variantId = null, quantity = 1, note = "", removeIngredientIds = [], addIngredientIds = [] }) =>
        http.post("/api/cart/items/pizza", { productId, variantId, quantity, note, removeIngredientIds, addIngredientIds }),
    updateItem: (itemId, patch) => http.patch(`/api/cart/items/${itemId}`, patch),
    removeItem: (itemId) => http.del(`/api/cart/items/${itemId}`),
    checkout: ({ phone, address }) =>
        http.post("/api/cart/checkout", { phone, address }),
};
