// src/pages/Menu.jsx
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { catalogApi, productApi } from "../api/catalog";
import { useCart } from "../context/CartContext";
import "../styles/menu.css";

const FallbackImg = "images/fallBackImg.png";

const SORTS = [
    { key: "new", label: "Newest" },
    { key: "priceAsc", label: "Price ↑" },
    { key: "priceDesc", label: "Price ↓" },
    { key: "name", label: "Name A–Z" },
];

function useCatalog() {
    const [pizzas, setPizzas] = useState([]);
    const [drinks, setDrinks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let mounted = true;
        (async () => {
            try {
                setLoading(true);
                setError(null);
                const [pz, dr] = await Promise.all([
                    catalogApi.pizzas(true),
                    catalogApi.drinks(true),
                ]);
                if (!mounted) return;
                setPizzas(pz ?? []);
                setDrinks(dr ?? []);
            } catch (e) {
                console.error(e);
                if (mounted) setError(e?.message || "Error loading menu.");
            } finally {
                if (mounted) setLoading(false);
            }
        })();
        return () => {
            mounted = false;
        };
    }, []);

    return { pizzas, drinks, loading, error };
}

function sortItems(items, sortBy) {
    const copy = items.slice();
    const getPrice = (item) => item?.price ?? item?.basePrice ?? 0;

    switch (sortBy) {
        case "priceAsc":
            return copy.sort((a, b) => getPrice(a) - getPrice(b));
        case "priceDesc":
            return copy.sort((a, b) => getPrice(b) - getPrice(a));
        case "name":
            return copy.sort((a, b) =>
                String(a?.name || "").localeCompare(String(b?.name || ""))
            );
        case "new":
        default:
            return copy.sort((a, b) => (b?.id ?? 0) - (a?.id ?? 0));
    }
}

function ProductCard({ item, onOpenQuick, ctaLabel = "Add", onAdd }) {
    const price = Number(item?.price ?? item?.basePrice ?? 0).toFixed(2);

    const onAddClick = (e) => {
        e.stopPropagation();
        onAdd?.(item);
    };

    return (
        <div
            className="card card--clickable"
            role="button"
            tabIndex={0}
            onClick={() => onOpenQuick?.(item)}
            onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") onOpenQuick?.(item);
            }}
        >
            <div className="media">
                <img
                    src={item?.imageUrl || FallbackImg}
                    alt={item?.name}
                    loading="lazy"
                />
            </div>
            <div className="body">
                <div className="row1">
                    <h4 className="title">{item?.name}</h4>
                    <span className="price">{price} BGN</span>
                </div>
                {item?.description && <p className="desc">{item.description}</p>}
                <div className="row2">
                    <button className="btn primary" onClick={onAddClick}>
                        {ctaLabel}
                    </button>
                </div>
            </div>
        </div>
    );
}
function QuickModal({
                        item,
                        pizzaDetails,
                        selectedVariantId,
                        setSelectedVariantId,
                        onAdd,
                        onDetails,
                        onClose,
                        loading,
                        error,
                        adding,
                    }) {
    const isPizza = !!item?.basePrice;

    const base = Number(pizzaDetails?.basePrice ?? item?.basePrice ?? 0);
    const selectedVariant = pizzaDetails?.variants?.find(
        (v) => String(v.id) === String(selectedVariantId)
    );
    const extra = Number(selectedVariant?.extraPrice || 0);
    const finalPrice = (base + extra).toFixed(2);
    const variantLabel = (v) =>
        v?.name || [v?.size, v?.dough].filter(Boolean).join(" · ");

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal-window" onClick={(e) => e.stopPropagation()}>
                <button className="modal-close" onClick={onClose} aria-label="Close">
                    ×
                </button>

                <div className="modal-header">
                    <img src={item?.imageUrl || FallbackImg} alt={item?.name} />
                    <div>
                        <h3>{item?.name}</h3>
                        {item?.description && <p className="muted">{item.description}</p>}
                    </div>
                </div>

                {loading && <p>Loading…</p>}
                {error && <p className="alert error">{error}</p>}

                {isPizza && !loading && !error && (
                    <div className="modal-body">
                        {pizzaDetails?.variants?.length ? (
                            <>
                                <label className="block">
                                    Variant:
                                    <select
                                        value={selectedVariantId ?? ""}
                                        onChange={(e) => setSelectedVariantId(e.target.value)}
                                    >
                                        {pizzaDetails.variants.map((v) => (
                                            <option key={v.id} value={v.id}>
                                                {variantLabel(v)}
                                                {Number(v.extraPrice) > 0
                                                    ? ` (+${Number(v.extraPrice).toFixed(2)} BGN)`
                                                    : ""}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                                <div className="price-row">
                                    <span>Total:</span>
                                    <strong>{finalPrice} BGN</strong>
                                </div>
                            </>
                        ) : (
                            <p className="muted">No variants available.</p>
                        )}
                    </div>
                )}

                {!isPizza && !loading && !error && (
                    <div className="modal-body">
                        <div className="price-row">
                            <span>Price:</span>
                            <strong>{Number(item?.price || 0).toFixed(2)} BGN</strong>
                        </div>
                    </div>
                )}

                <div className="modal-actions">
                    <button
                        className="btn primary"
                        onClick={() => onAdd(item, selectedVariant)}
                        disabled={loading || adding}
                    >
                        {adding ? "Adding…" : "Add to cart"}
                    </button>
                    <button className="btn outline" onClick={() => onDetails(item)}>
                        Details
                    </button>
                </div>
            </div>
        </div>
    );
}

export default function Menu() {
    const navigate = useNavigate();
    const cart = useCart();

    const { pizzas, drinks, loading, error } = useCatalog();
    const [sortByPizzas, setSortByPizzas] = useState("new");
    const [sortByDrinks, setSortByDrinks] = useState("new");
    const [query, setQuery] = useState("");

    // modal state
    const [quickItem, setQuickItem] = useState(null); // {id, name, ...}
    const [quickPizza, setQuickPizza] = useState(null); // pizza details with variants
    const [quickVariantId, setQuickVariantId] = useState(null);
    const [quickLoading, setQuickLoading] = useState(false);
    const [quickError, setQuickError] = useState(null);
    const [adding, setAdding] = useState(false);

    const filteredPizzas = useMemo(() => {
        const list = sortItems(pizzas, sortByPizzas);
        if (!query) return list;
        const q = query.toLowerCase();
        return list.filter(
            (x) =>
                (x?.name || "").toLowerCase().includes(q) ||
                (x?.description || "").toLowerCase().includes(q)
        );
    }, [pizzas, sortByPizzas, query]);

    const filteredDrinks = useMemo(() => {
        const list = sortItems(drinks, sortByDrinks);
        if (!query) return list;
        const q = query.toLowerCase();
        return list.filter(
            (x) =>
                (x?.name || "").toLowerCase().includes(q) ||
                (x?.description || "").toLowerCase().includes(q)
        );
    }, [drinks, sortByDrinks, query]);

    const onAddToCart = async (item, variant = null) => {
        try {
            setAdding(true);
            if (item?.basePrice != null) {
                let v = variant;
                if (!v) {
                    const p = await productApi.pizza(item.id, true);
                    v =
                        Array.isArray(p?.variants) && p.variants.length ? p.variants[0] : null;
                }
                await cart.addPizza({
                    productId: item.id,
                    variantId: v ? v.id : null,
                    quantity: 1,
                    removeIngredientIds: [],
                    addIngredientIds: [],
                });
            } else {
                await cart.addDrink({ productId: item.id, quantity: 1 });
            }
            setQuickItem(null);
        } catch (e) {
            alert(e?.data?.error || e?.message || "Failed to add to cart");
        } finally {
            setAdding(false);
        }
    };

    const openQuickModal = async (item) => {
        setQuickItem(item);
        setQuickError(null);
        setQuickPizza(null);
        setQuickVariantId(null);

        if (item?.basePrice != null) {
            try {
                setQuickLoading(true);
                const p = await productApi.pizza(item.id, true);
                setQuickPizza(p);
                if (p?.variants?.length) setQuickVariantId(String(p.variants[0].id));
            } catch (e) {
                setQuickError(
                    e?.data?.error || e?.message || "Failed to load pizza details."
                );
            } finally {
                setQuickLoading(false);
            }
        }
    };

    const closeQuickModal = () => {
        setQuickItem(null);
    };

    const goDetails = (item) => {
        const path = item?.basePrice != null ? `/pizza/${item.id}` : `/drink/${item.id}`;
        navigate(path);
    };

    return (
        <div className="menu-page">
            <header className="menu-header">
                <h2>Menu</h2>
                <p className="subtitle">
                    Browse our pizzas and drinks. Filter, sort and pick your favorites.
                </p>
                <div className="toolbar">
                    <div className="search">
                        <input
                            type="search"
                            placeholder="Search by name or description…"
                            value={query}
                            onChange={(e) => setQuery(e.target.value)}
                            aria-label="Search in menu"
                        />
                    </div>
                </div>
            </header>

            {error && <div className="alert error">{error}</div>}
            {loading && (
                <div className="skeleton-grid" aria-busy>
                    {Array.from({ length: 6 }).map((_, i) => (
                        <div key={i} className="skeleton-card" />
                    ))}
                </div>
            )}

            {!loading && (
                <>
                    <section className="section" id="pizzas">
                        <div className="section-head">
                            <h3>Pizzas</h3>
                            <div className="controls">
                                <label>
                                    Sort by:
                                    <select
                                        value={sortByPizzas}
                                        onChange={(e) => setSortByPizzas(e.target.value)}
                                    >
                                        {SORTS.map((s) => (
                                            <option key={s.key} value={s.key}>
                                                {s.label}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                            </div>
                        </div>

                        {filteredPizzas.length === 0 ? (
                            <p className="muted">No pizzas found.</p>
                        ) : (
                            <div className="grid">
                                {filteredPizzas.map((p) => (
                                    <ProductCard
                                        key={p.id}
                                        item={p}
                                        ctaLabel="Add pizza"
                                        onAdd={onAddToCart}
                                        onOpenQuick={openQuickModal}
                                    />
                                ))}
                            </div>
                        )}
                    </section>

                    <section className="section" id="drinks">
                        <div className="section-head">
                            <h3>Drinks</h3>
                            <div className="controls">
                                <label>
                                    Sort by:
                                    <select
                                        value={sortByDrinks}
                                        onChange={(e) => setSortByDrinks(e.target.value)}
                                    >
                                        {SORTS.map((s) => (
                                            <option key={s.key} value={s.key}>
                                                {s.label}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                            </div>
                        </div>

                        {filteredDrinks.length === 0 ? (
                            <p className="muted">No drinks found.</p>
                        ) : (
                            <div className="grid">
                                {filteredDrinks.map((d) => (
                                    <ProductCard
                                        key={d.id}
                                        item={d}
                                        ctaLabel="Add drink"
                                        onAdd={onAddToCart}
                                        onOpenQuick={openQuickModal}
                                    />
                                ))}
                            </div>
                        )}
                    </section>
                </>
            )}


            {quickItem && (
                <QuickModal
                    item={quickItem}
                    pizzaDetails={quickPizza}
                    selectedVariantId={quickVariantId}
                    setSelectedVariantId={setQuickVariantId}
                    onAdd={onAddToCart}
                    onDetails={goDetails}
                    onClose={closeQuickModal}
                    loading={quickLoading}
                    error={quickError}
                    adding={adding}
                />
            )}
        </div>
    );
}
