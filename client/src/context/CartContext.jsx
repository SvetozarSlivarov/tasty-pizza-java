// src/store/CartContext.jsx
import { createContext, useContext, useEffect, useMemo, useReducer, useCallback } from "react";
import { cartApi } from "../api/cart";

const CartContext = createContext(null);

const initialState = {
    isOpen: false,
    loading: false,
    items: [],
    subtotal: 0,
    orderId: null,
    status: null,
    error: null,
};

function mapServerCart(data) {
    const items = (data?.items ?? []).map((it) => {
        const id = it.id ?? it.itemId ?? it.cartItemId;
        const qty = it.quantity ?? it.qty ?? 1;
        const unitPrice = Number(it.unitPrice ?? it.price ?? 0);
        const name = it.name ?? it.productName ?? it.pizzaName ?? it.drinkName ?? "Item";
        const imageUrl = it.imageUrl ?? it.photo ?? it.thumbnailUrl ?? null;

        const size = it.size ?? it.variant?.size;
        const dough = it.dough ?? it.variant?.dough;
        const variantName = it.variantName ?? it.variant?.name;
        const variantLabel = variantName || [size, dough].filter(Boolean).join(" · ") || null;

        const type = it.type ?? ((it.pizzaVariantId != null || it.variantId != null || it.variant) ? "pizza" : "drink");

        // нужни за Edit
        const productId = it.productId ?? it.pizzaId ?? null;
        const pizzaVariantId = it.pizzaVariantId ?? it.variantId ?? null;
        const note = it.note ?? "";
        const customizations = Array.isArray(it.customizations)
            ? it.customizations.map(c => ({
                ingredientId: c.ingredientId ?? c.id ?? c.ingredientID ?? null,
                action: (c.action ?? "").toLowerCase(),
            }))
            : [];

        return {
            id,
            name,
            imageUrl,
            qty,
            unitPrice,
            type,
            variantLabel,
            productId,
            pizzaVariantId,
            note,
            customizations,
        };
    });

    const subtotal =
        (typeof data?.total === "number" ? data.total :
            items.reduce((s, i) => s + i.unitPrice * i.qty, 0));

    return {
        items,
        subtotal,
        orderId: data?.orderId ?? null,
        status: data?.status ?? null,
    };
}

function reducer(state, action) {
    switch (action.type) {
        case "OPEN": return { ...state, isOpen: true };
        case "CLOSE": return { ...state, isOpen: false };
        case "LOADING": return { ...state, loading: true, error: null };
        case "ERROR": return { ...state, loading: false, error: action.payload || "Error" };
        case "SET_CART": return { ...state, loading: false, error: null, ...action.payload };
        default: return state;
    }
}

export function CartProvider({ children }) {
    const [state, dispatch] = useReducer(reducer, initialState);

    const refresh = useCallback(async () => {
        try {
            dispatch({ type: "LOADING" });
            const data = await cartApi.get();
            dispatch({ type: "SET_CART", payload: mapServerCart(data) });
        } catch (e) {
            dispatch({ type: "ERROR", payload: e?.data?.error || e?.message });
        }
    }, []);

    useEffect(() => { refresh(); }, [refresh]);

    const api = useMemo(() => ({
        // UI
        isOpen: state.isOpen,
        open: () => dispatch({ type: "OPEN" }),
        close: () => dispatch({ type: "CLOSE" }),
        toggle: () => dispatch({ type: state.isOpen ? "CLOSE" : "OPEN" }),

        loading: state.loading,
        error: state.error,
        items: state.items,
        subtotal: state.subtotal,
        count: state.items.reduce((s, i) => s + i.qty, 0),
        orderId: state.orderId,
        status: state.status,

        refresh,

        async addPizza({ productId, variantId = null, quantity = 1, removeIngredientIds = [], addIngredientIds = [], note = "" }) {
            await cartApi.addPizza({ productId, variantId, quantity, removeIngredientIds, addIngredientIds, note });
            await refresh();
            dispatch({ type: "OPEN" });
        },

        async addDrink({ productId, quantity = 1, note = "" }) {
            await cartApi.addDrink({ productId, quantity, note });
            await refresh();
            dispatch({ type: "OPEN" });
        },

        async updateQty(itemId, qty) {
            const q = Math.max(1, Number(qty) || 1);
            await cartApi.updateItem(itemId, { quantity: q });
            await refresh();
        },

        async remove(itemId) {
            await cartApi.removeItem(itemId);
            await refresh();
        },

        async clear() {
            const ids = state.items.map(i => i.id);
            for (const id of ids) { try { await cartApi.removeItem(id); } catch {} }
            await refresh();
        },

        async checkout({ phone, address }) {
            const res = await cartApi.checkout({ phone, address });
            await refresh();
            return res;
        },
    }), [state.isOpen, state.loading, state.error, state.items, state.subtotal, state.orderId, state.status, refresh]);

    return <CartContext.Provider value={api}>{children}</CartContext.Provider>;
}

export function useCart() {
    const ctx = useContext(CartContext);
    if (!ctx) throw new Error("useCart must be used within CartProvider");
    return ctx;
}
