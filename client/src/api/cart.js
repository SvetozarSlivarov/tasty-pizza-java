import { http } from "./http";

function toQty(val) {
    if (val == null) return undefined;
    const n = Number(val);
    return Number.isFinite(n) ? n : undefined;
}
function mapAddPizzaPayload(p) {
    return {
        productId: p.productId,
        variantId: p.variantId ?? null,
        qty: toQty(p.qty ?? p.quantity) ?? 1,
        note: p.note ?? "",
        removeIds: p.removeIds ?? p.removeIngredientIds ?? [],
        addIds: p.addIds ?? p.addIngredientIds ?? [],
    };
}
function mapAddDrinkPayload(p) {
    return {
        productId: p.productId,
        qty: toQty(p.qty ?? p.quantity) ?? 1,
        note: p.note ?? "",
    };
}
function mapPatchPayload(patch) {
    const out = { ...patch };
    // quantity -> qty
    if (out.qty == null && out.quantity != null) {
        out.qty = toQty(out.quantity);
        delete out.quantity;
    }
    if (out.addIds == null && Array.isArray(out.addIngredientIds)) {
        out.addIds = out.addIngredientIds;
        delete out.addIngredientIds;
    }
    if (out.removeIds == null && Array.isArray(out.removeIngredientIds)) {
        out.removeIds = out.removeIngredientIds;
        delete out.removeIngredientIds;
    }
    return out;
}

export const cartApi = {
    get: () => http.get("/api/cart"),

    addDrink: (payload) =>
        http.post("/api/cart/items/drink", mapAddDrinkPayload(payload)),

    addPizza: (payload) =>
        http.post("/api/cart/items/pizza", mapAddPizzaPayload(payload)),

    updateItem: (itemId, patch) =>
        http.patch(`/api/cart/items/${itemId}`, mapPatchPayload(patch)),

    removeItem: (itemId) => http.del(`/api/cart/items/${itemId}`),
    checkout: ({ phone, address }) =>
        http.post("/api/cart/checkout", { phone, address }),
};