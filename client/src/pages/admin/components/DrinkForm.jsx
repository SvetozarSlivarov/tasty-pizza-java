import { useMemo, useState } from "react";
import styles from "../../../styles/Drinks.module.css";

export function normalizeDrink(d) {
    return {
        name: d?.name ?? "",
        description: d?.description ?? "",
        price: typeof d?.price === "number" ? d.price : Number(d?.price) || 0,
        isAvailable: Boolean(d?.isAvailable ?? true),
    };
}

const PRICE_MIN = 0.01;
const PRICE_MAX = 1000;
const IMAGE_MAX_BYTES = 5 * 1024 * 1024; // 5MB

export default function DrinkForm({ initial, onSubmit, onCancel }) {
    const [model, setModel] = useState(normalizeDrink(initial));
    const [imageFile, setImageFile] = useState(null);
    const [busy, setBusy] = useState(false);
    const [errors, setErrors] = useState({});

    function update(k, v) {
        setModel((m) => ({ ...m, [k]: v }));
    }

    function validate(next = model) {
        const e = {};
        const name = String(next.name || "").trim();
        if (name.length < 2) e.name = "Name must be at least 2 characters.";
        if (name.length > 60) e.name = "Name cannot exceed 60 characters.";

        const priceNum = Number(next.price);
        if (!Number.isFinite(priceNum)) e.price = "Price must be a number.";
        else if (priceNum < PRICE_MIN) e.price = `Price cannot be negative or zero.`;
        else if (priceNum > PRICE_MAX) e.price = `Price cannot exceed ${PRICE_MAX.toFixed(2)}.`;

        if (next.description && String(next.description).length > 300) {
            e.description = "Description is too long (max 300 characters).";
        }

        if (imageFile) {
            if (!/^image\//.test(imageFile.type)) e.image = "Only image files are allowed.";
            if (imageFile.size > IMAGE_MAX_BYTES) e.image = "Image must be ≤ 5MB.";
        }
        setErrors(e);
        return e;
    }

    const canSave = useMemo(() => {
        const e = validate(model);
        return Object.keys(e).length === 0;
    }, [model, imageFile]);

    function onPriceChange(raw) {
        const val = raw.replace(",", ".");
        const num = Number(val);
        update("price", Number.isFinite(num) ? val : "");
    }

    function onPriceBlur() {
        const n = Number(model.price);
        if (!Number.isFinite(n)) return;
        const bounded = Math.min(Math.max(n, PRICE_MIN), PRICE_MAX);
        update("price", bounded.toFixed(2));
    }

    function onImagePick(file) {
        setImageFile(file || null);
        setTimeout(() => validate(model), 0);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        const eMap = validate(model);
        if (Object.keys(eMap).length > 0) return;
        try {
            setBusy(true);
            await onSubmit?.(normalizeDrink({
                ...model,
                price: Number(model.price),
            }), imageFile);
        } finally {
            setBusy(false);
        }
    }

    return (
        <form className={styles.form} onSubmit={handleSubmit}>
            {/* Name */}
            <div>
                <label className={styles.label}>Name</label>
                <input
                    className={styles.input}
                    value={model.name}
                    onChange={(e) => update("name", e.target.value)}
                    placeholder="Mango Basil Cooler"
                />
                {errors.name && (
                    <div className={styles.note} style={{ color: "#ff8aa6" }}>{errors.name}</div>
                )}
            </div>

            {/* Description */}
            <div>
                <label className={styles.label}>Description</label>
                <textarea
                    className={styles.input}
                    rows={3}
                    value={model.description}
                    onChange={(e) => update("description", e.target.value)}
                    placeholder="Refreshing sparkling drink with mango, basil and lime."
                />
                {errors.description && (
                    <div className={styles.note} style={{ color: "#ff8aa6" }}>{errors.description}</div>
                )}
            </div>

            {/* Price */}
            <div>
                <label className={styles.label}>Price (BGN)</label>
                <input
                    className={styles.input}
                    type="text"
                    inputMode="decimal"
                    value={String(model.price)}
                    onChange={(e) => onPriceChange(e.target.value)}
                    onBlur={onPriceBlur}
                    placeholder="4.90"
                />
                {errors.price && (
                    <div className={styles.note} style={{ color: "#ff8aa6" }}>{errors.price}</div>
                )}
                <div className={styles.note}>
                    Allowed range: {PRICE_MIN.toFixed(2)} – {PRICE_MAX.toFixed(2)} BGN
                </div>
            </div>

            {/* Available */}
            <div className={styles.row}>
                <label className={styles.label} style={{ margin: 0 }}>Available</label>
                <input
                    type="checkbox"
                    checked={model.isAvailable}
                    onChange={(e) => update("isAvailable", e.target.checked)}
                    style={{ marginLeft: 8 }}
                />
            </div>

            {/* Image (optional) */}
            <div>
                <label className={styles.label}>Image (optional)</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => onImagePick(e.target.files?.[0] || null)}
                />
                {errors.image && (
                    <div className={styles.note} style={{ color: "#ff8aa6" }}>{errors.image}</div>
                )}
                <div className={styles.note}>If provided, it will upload right after save. Max 5MB.</div>
            </div>

            {/* Actions */}
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
