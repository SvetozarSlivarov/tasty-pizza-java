import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { productApi } from "../api/catalog";
import "../styles/details.css";

export default function PizzaDetails() {
    const { id } = useParams();
    const [pizza, setPizza] = useState(null);
    const [ingredients, setIngredients] = useState([]);
    const [allowed, setAllowed] = useState([]);
    const [selectedVariant, setSelectedVariant] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let mounted = true;
        setLoading(true);
        setError(null);

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
                else setError(e?.data?.error || e?.message || "");
            } finally {
                if (mounted) setLoading(false);
            }
        }

        load();
        return () => { mounted = false; };
    }, [id]);

    if (loading) return <p>Loading...</p>;
    if (error) return (
        <div className="details-content">
            <p style={{color:"#b00020"}}>{error}</p>
            <Link to="/menu">← Back to menu</Link>
        </div>
    );
    if (!pizza) return null;

    const extra = Number(selectedVariant?.extraPrice || 0);
    const base = Number(pizza.basePrice || 0);
    const finalPrice = base + extra;

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
                                    {"Size: "+ v.size + "  --  "+ "Dough: " + v.dough}{Number(v.extraPrice) > 0 ? `   (+${Number(v.extraPrice).toFixed(2)} BGN)` : ""}
                                </option>
                            ))}
                        </select>
                    </div>
                )}

                <p className="price">Price: {finalPrice.toFixed(2)} BGN</p>

                <div className="ingredients">
                    <h3>Base ingredients:</h3>
                    <ul>
                        {ingredients.map((ing) => (
                            <li key={ing.id}>
                                {ig(ing.name)} {!ing.removable && <em>(fixed)</em>}
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="ingredients">
                    <h3>Allowed extras:</h3>
                    <ul>
                        {allowed.map((a) => (
                            <li key={a.id}>{ig(a.name)}</li>
                        ))}
                    </ul>
                </div>

                <button className="btn" disabled={pizza.isAvailable === false}>
                    Add to cart
                </button>
                <div>
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
