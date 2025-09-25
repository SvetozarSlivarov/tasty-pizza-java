import { useMemo, useState } from "react";
import styles from "../../../styles/Pizzas.module.css";

export function normalizeDrink(d) {
    return {
        name: d?.name ?? "",
        description: d?.description ?? "",
        price: typeof d?.price === "number" ? d.price : Number(d?.price) || 0,
        isAvailable: Boolean(d?.isAvailable ?? true),
    };
}

export default function DrinkForm({ initial, onSubmit, onCancel }) {
    const [model, setModel] = useState(normalizeDrink(initial));
    const [imageFile, setImageFile] = useState(null);
    const [busy, setBusy] = useState(false);

    const canSave = useMemo(() => {
        return model.name.trim().length >= 2 && Number.isFinite(Number(model.price));
    }, [model]);

    function update(k, v) {
        setModel((m) => ({ ...m, [k]: v }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (!canSave) return;
        try {
            setBusy(true);
            await onSubmit?.(normalizeDrink(model), imageFile);
        } finally {
            setBusy(false);
        }
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.row}>
                <label className={styles.label}>Name</label>
                <input
                    className={styles.input}
                    value={model.name}
                    onChange={(e) => update("name", e.target.value)}
                    placeholder="Coca Cola"
                />
            </div>

            <div className={styles.row}>
                <label className={styles.label}>Description</label>
                <textarea
                    className={styles.input}
                    rows={3}
                    value={model.description}
                    onChange={(e) => update("description", e.target.value)}
                    placeholder="Refreshing soda 500ml"
                />
            </div>

            <div className={styles.row}>
                <label className={styles.label}>Price</label>
                <input
                    className={styles.input}
                    type="number"
                    step="0.10"
                    value={model.price}
                    onChange={(e) => update("price", Number(e.target.value).toFixed(2))}
                />
            </div>

            <div className={styles.row}>
                <label className={styles.label}>Available</label>
                <input
                    type="checkbox"
                    checked={model.isAvailable}
                    onChange={(e) => update("isAvailable", e.target.checked)}
                />
            </div>

            <div className={styles.row}>
                <label className={styles.label}>Image (optional)</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setImageFile(e.target.files?.[0] || null)}
                />
                <div className={styles.note}>If provided, it will upload right after save.</div>
            </div>

            <div className={styles.row}>
                <button
                    className={`${styles.btn} ${styles.btnPrimary}`}
                    type="submit"
                    disabled={busy || !canSave}
                >
                    {busy ? "Saving..." : "Save"}
                </button>
                <button
                    className={styles.btn}
                    type="button"
                    onClick={onCancel}
                    disabled={busy}
                >
                    Cancel
                </button>
            </div>
        </form>
    );
}