import { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { productApi } from "../api/catalog";
import { cartApi } from "../api/cart";
import "../styles/details.css";

export default function PizzaDetails() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [pizza, setPizza] = useState(null);
    const [ingredients, setIngredients] = useState([]);
    const [allowed, setAllowed] = useState([]);
    const [selectedVariant, setSelectedVariant] = useState(null);

    const [removeIds, setRemoveIds] = useState(new Set());
    const [addIds, setAddIds] = useState(new Set());

    const [qty, setQty] = useState(1);
    const [note, setNote] = useState("");

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let mounted = true;
        setLoading(true);
        setError(null);
        setRemoveIds(new Set());
        setAddIds(new Set());
        setQty(1);
        setNote("");

        async function load() {
            try {
                const [p, ingr, allow] = await Promise.all([
                    productApi.pizza(id, true),
                    productApi.pizzaIngredients(id),
                    productApi.pizzaAllowedIngredients(id),
                ]);
                if (!mounted) return;

                setPizza(p || null);

                const normalizeIngredient = (x) => ({
                    id: x?.id ?? x?.ingredientId ?? x?.ingredientID ?? cryptoRandom(),
                    name: x?.name ?? "",
                    removable: x?.removable ?? x?.isRemovable ?? false,
                });

                setIngredients(Array.isArray(ingr) ? ingr.map(normalizeIngredient) : []);
                setAllowed(Array.isArray(allow) ? allow.map((a) => ({
                    id: a?.id ?? a?.ingredientId ?? cryptoRandom(),
                    name: a?.name ?? "",
                })) : []);

                if (p?.variants?.length) {
                    setSelectedVariant(p.variants[0]);
                } else {
                    setSelectedVariant(null);
                }
            } catch (e) {
                if (!mounted) return;
                if (e?.status === 404) setError("This pizza not found! (404).");
                else setError(e?.data?.error || e?.message || "Failed to load pizza.");
            } finally {
                if (mounted) setLoading(false);
            }
        }

        load();
        return () => { mounted = false; };
    }, [id]);

    const extra = Number(selectedVariant?.extraPrice || 0);
    const base = Number(pizza?.basePrice || 0);
    const finalPrice = base + extra;

    const toggleBase = (ingId, isRemovable) => {
        if (!isRemovable) return; // защитa в UI
        setRemoveIds(prev => {
            const next = new Set(prev);
            next.has(ingId) ? next.delete(ingId) : next.add(ingId);
            return next;
        });
    };

    const toggleAllowed = (ingId) => {
        setAddIds(prev => {
            const next = new Set(prev);
            next.has(ingId) ? next.delete(ingId) : next.add(ingId);
            return next;
        });
    };

    const onAddToCart = async () => {
        try {
            setSubmitting(true);
            setError(null);

            const toAddArr = Array.from(addIds);
            const toRemoveArr = Array
                .from(removeIds)
                .filter(x => !addIds.has(x));

            const payload = {
                productId: Number(id),
                variantId: selectedVariant?.id ?? null,
                quantity: Math.max(1, Number(qty) || 1),
                note: (note ?? "").trim(),
                removeIngredientIds: toRemoveArr,
                addIngredientIds: toAddArr,
            };

            await cartApi.addPizza(payload);
            navigate("/menu");
        } catch (e) {
            setError(e?.data?.error || e?.message || "Failed to add to cart.");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <p>Loading...</p>;
    if (error && !pizza) return (
        <div className="details-content">
            <p style={{color:"#b00020"}}>{error}</p>
            <Link to="/menu">← Back to menu</Link>
        </div>
    );
    if (!pizza) return null;

    return (
        <div className="details-container">
            <img src={pizza.imageUrl} alt={pizza.name} className="details-image" />
            <div className="details-content">
                <h1>{pizza.name}</h1>
                <p>{pizza.description}</p>

                <div className="tags">
                    {pizza.isSpicy && <span className="tag spicy">🌶 Spicy</span>}
                    {pizza.isAvailable === false && <span className="tag unavailable">Unavailable</span>}
                </div>

                {pizza.variants?.length > 0 && (
                    <div className="variants">
                        <label>Choose size / crust:</label>
                        <select
                            value={selectedVariant?.id ?? ""}
                            onChange={(e) => {
                                const v = pizza.variants.find(v => String(v.id) === e.target.value);
                                setSelectedVariant(v || null);
                            }}
                        >
                            {pizza.variants.map((v) => (
                                <option key={v.id} value={v.id}>
                                    {"Size: " + v.size + "  --  " + "Dough: " + v.dough}
                                    {Number(v.extraPrice) > 0 ? `   (+${Number(v.extraPrice).toFixed(2)} BGN)` : ""}
                                </option>
                            ))}
                        </select>
                    </div>
                )}

                <p className="price">Price: {finalPrice.toFixed(2)} BGN</p>

                <div className="ingredients">
                    <h3>Base ingredients:</h3>
                    {ingredients.length === 0 ? <p>—</p> : (
                        <ul>
                            {ingredients.map((ing) => (
                                <li key={ing.id}>
                                    <label>
                                        <input
                                            type="checkbox"
                                            checked={!removeIds.has(ing.id)}
                                            disabled={!ing.removable}
                                            onChange={() => toggleBase(ing.id, ing.removable)}
                                        />
                                        {" "}{ig(ing.name)}{" "}
                                        {!ing.removable && <em>(fixed)</em>}
                                    </label>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                <div className="ingredients">
                    <h3>Allowed extras:</h3>
                    {allowed.length === 0 ? <p>—</p> : (
                        <ul>
                            {allowed.map((a) => (
                                <li key={a.id}>
                                    <label>
                                        <input
                                            type="checkbox"
                                            checked={addIds.has(a.id)}
                                            onChange={() => toggleAllowed(a.id)}
                                        />
                                        {" "}{ig(a.name)}
                                    </label>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                <div className="note-qty" style={{ display: "grid", gap: 12, gridTemplateColumns: "1fr 2fr" }}>
                    <div>
                        <label>Quantity</label>
                        <input
                            type="number"
                            min="1"
                            value={qty}
                            onChange={(e) => setQty(e.target.value)}
                            style={{ width: "100%" }}
                        />
                    </div>
                    <div>
                        <label>Note</label>
                        <input
                            type="text"
                            value={note}
                            onChange={(e) => setNote(e.target.value)}
                            placeholder="e.g. cut into 8 slices"
                            style={{ width: "100%" }}
                        />
                    </div>
                </div>

                {error && <p style={{ color: "#b00020", marginTop: 8 }}>{error}</p>}

                <button
                    className="btn"
                    disabled={pizza.isAvailable === false || submitting}
                    onClick={onAddToCart}
                >
                    {submitting ? "Adding..." : "Add to cart"}
                </button>

                <div style={{ marginTop: 8 }}>
                    <Link to="/menu">← Back to menu</Link>
                </div>
            </div>
        </div>
    );
}

/** helpers **/
function ig(s) { return (s ?? "").trim() || "—"; }
function cryptoRandom() {
    return Math.random().toString(36).slice(2);
}
