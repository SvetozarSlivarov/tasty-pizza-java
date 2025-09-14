import { useEffect } from "react";
import { useCart } from "../context/CartContext";
import "../styles/cart.css";

export default function CartDrawer() {
    const cart = useCart();

    useEffect(() => {
        if (cart.isOpen) cart.refresh().catch(() => {});
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [cart.isOpen]);

    return (
        <>
            <div
                className={`cart-backdrop ${cart.isOpen ? "open" : ""}`}
                onClick={cart.close}
            />

            {/* drawer */}
            <aside className={`cart-drawer ${cart.isOpen ? "open" : ""}`} aria-hidden={!cart.isOpen}>
                <header className="cart-header">
                    <h3>Your cart</h3>
                    <button className="icon-btn" onClick={cart.close} aria-label="Close">×</button>
                </header>

                <div className="cart-body">
                    {cart.loading && <p className="muted">Loading…</p>}
                    {cart.error && <p className="alert error">{cart.error}</p>}

                    {!cart.loading && cart.items.length === 0 && (
                        <div className="empty">
                            <p>Your cart is empty.</p>
                            <p className="muted">Add items from the menu.</p>
                        </div>
                    )}

                    {!cart.loading && cart.items.length > 0 && (
                        <ul className="cart-list">
                            {console.log(cart.items)}
                            {cart.items.map((it) => (
                                <li key={it.id} className="cart-item">
                                    <div className="ci-media">
                                        {it.imageUrl ? (
                                            <img src={it.imageUrl} alt={it.name} />
                                        ) : (
                                            <div className="ci-placeholder" aria-hidden />
                                        )}
                                    </div>

                                    <div className="ci-main">
                                        <div className="ci-title">
                                            <strong>{it.name}</strong>
                                            {it.variantLabel && <span className="muted"> · {it.variantLabel}</span>}
                                        </div>

                                        <div className="ci-meta">
                                            <div className="qty">
                                                <button className="btn sm" onClick={() => cart.updateQty(it.id, it.qty - 1)} aria-label="Decrease">–</button>
                                                <input
                                                    type="number"
                                                    min={1}
                                                    value={it.qty}
                                                    onChange={(e) => cart.updateQty(it.id, e.target.value)}
                                                    aria-label="Quantity"
                                                />
                                                <button className="btn sm" onClick={() => cart.updateQty(it.id, it.qty + 1)} aria-label="Increase">+</button>
                                            </div>
                                            <div className="price">
                                                {(it.unitPrice * it.qty).toFixed(2)} BGN
                                                <span className="muted per">({it.unitPrice.toFixed(2)} ea)</span>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="ci-actions">
                                        <button className="icon-btn" onClick={() => cart.remove(it.id)} aria-label="Remove">🗑️</button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

                <footer className="cart-footer">
                    <div className="row">
                        <span>Subtotal</span>
                        <strong>{cart.subtotal.toFixed(2)} BGN</strong>
                    </div>
                    <div className="row actions">
                        <button className="btn outline" onClick={cart.clear} disabled={cart.items.length === 0}>
                            Clear
                        </button>
                        <button
                            className="btn primary"
                            onClick={async () => {
                                try {
                                    // TODO: ако имаш форма — попитай за телефон/адрес
                                    await cart.checkout({ phone: "", address: "" });
                                    alert("Order placed! ✅");
                                } catch (e) {
                                    alert(e?.data?.error || e?.message || "Checkout failed");
                                }
                            }}
                            disabled={cart.items.length === 0}
                        >
                            Checkout
                        </button>
                    </div>
                </footer>
            </aside>
        </>
    );
}
