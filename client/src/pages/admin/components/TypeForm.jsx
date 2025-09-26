import { useMemo, useState } from "react";
import styles from "../../../styles/Pizzas.module.css";

export const DEFAULT_VARIANT = { size: "medium", dough: "classic", extraPrice: 0 };

export function hasValidVariants(variants) {
    const arr = Array.isArray(variants) ? variants : [];
    if (arr.length < 1) return false;
    return arr.every((v) => v?.size && v?.dough && Number.isFinite(v?.extraPrice ?? 0));
}

export function normalizePizza(p) {
    const variants = Array.isArray(p?.variants) ? p.variants : [];
    return {
        name: p?.name ?? "",
        basePrice: typeof p?.basePrice === "number" ? p.basePrice : 0,
        isAvailable: p?.isAvailable ?? true,
        description: p?.description ?? "",
        spicyLevel: p?.spicyLevel ?? "mild",
        imageUrl: p?.imageUrl ?? null,
        variants: variants.map((v) => ({
            size: v?.size ?? "",
            dough: v?.dough ?? "",
            extraPrice: typeof v?.extraPrice === "number" ? v.extraPrice : 0,
        })),
    };
}

export default function PizzaForm({
                                      initial,
                                      onSubmit,
                                      onCancel,
                                      busy,
                                      showImagePicker = false,
                                      onImagePicked,
                                  }) {
    const [form, setForm] = useState(() => {
        const base = normalizePizza(initial ?? {});
        const has = Array.isArray(base.variants) && base.variants.length > 0;
        return { ...base, variants: has ? base.variants : [DEFAULT_VARIANT] };
    });

    const canSave = useMemo(() => {
        return form.name.trim() && form.basePrice >= 0 && hasValidVariants(form.variants);
    }, [form]);

    const addVariant = () =>
        setForm((v) => ({ ...v, variants: [...v.variants, { ...DEFAULT_VARIANT }] }));

    const updateVariant = (i, patch) =>
        setForm((v) => ({
            ...v,
            variants: v.variants.map((it, idx) => (idx === i ? { ...it, ...patch } : it)),
        }));

    const removeVariant = (i) =>
        setForm((v) => {
            const next = v.variants.filter((_, idx) => idx !== i);
            return { ...v, variants: next.length ? next : [DEFAULT_VARIANT] };
        });

    const submit = (e) => {
        e.preventDefault();
        if (!canSave) {
            alert("Please fill all required fields. Pizza must have at least one valid variant.");
            return;
        }

        const mapSpicy = (v) => String(v || "").toUpperCase();
        const mapSize = (v) => String(v || "").toUpperCase();
        const mapDough = (v) => {
            const s = String(v || "").toLowerCase();
            if (s === "thin") return "THIN";
            if (s === "classic") return "CLASSIC";
            if (s === "wholegrain") return "WHOLEGRAIN";
            return s.toUpperCase();
        };

        const payload = {
            name: form.name,
            basePrice: form.basePrice,
            isAvailable: form.isAvailable,
            description: form.description,
            spicyLevel: mapSpicy(form.spicyLevel),
            imageUrl: form.imageUrl ?? null,
            variants: (form.variants ?? []).map(({ size, dough, extraPrice }) => ({
                size: mapSize(size),
                dough: mapDough(dough),
                extraPrice,
            })),
        };
        onSubmit(payload);
    };

    return (
        <form className={styles.form} onSubmit={submit}>
            <div className={styles.field}>
                <label>Name</label>
                <input
                    className={styles.input}
                    value={form.name}
                    onChange={(e) => setForm((v) => ({ ...v, name: e.target.value }))}
                    required
                />
            </div>

            <div className={styles.field}>
                <label>Base price</label>
                <input
                    type="number"
                    min="0"
                    step="0.01"
                    className={styles.input}
                    value={form.basePrice}
                    onChange={(e) =>
                        setForm((v) => ({
                            ...v,
                            basePrice: Number.isNaN(parseFloat(e.target.value))
                                ? 0
                                : parseFloat(e.target.value),
                        }))
                    }
                    required
                />
            </div>

            <div className={styles.row}>
                <input
                    id="available"
                    type="checkbox"
                    className={styles.checkbox}
                    checked={!!form.isAvailable}
                    onChange={(e) => setForm((v) => ({ ...v, isAvailable: e.target.checked }))}
                />
                <label htmlFor="available">Available</label>
            </div>

            <div className={styles.field}>
                <label>Spicy level</label>
                <select
                    className={styles.select}
                    value={form.spicyLevel}
                    onChange={(e) => setForm((v) => ({ ...v, spicyLevel: e.target.value }))}
                >
                    <option value="mild">mild</option>
                    <option value="medium">medium</option>
                    <option value="hot">hot</option>
                </select>
            </div>

            <div className={styles.field}>
                <label>Description (optional)</label>
                <textarea
                    className={styles.textarea}
                    rows={3}
                    value={form.description || ""}
                    onChange={(e) => setForm((v) => ({ ...v, description: e.target.value }))}
                />
            </div>

            {form.imageUrl && (
                <div className={styles.field}>
                    <label>Current image</label>
                    <img src={form.imageUrl} alt="pizza" className={styles.img} />
                </div>
            )}

            <div className={styles.field}>
                <label>Variants</label>
                <div className={styles.variants}>
                    {(form.variants ?? []).map((vr, i) => (
                        <div key={i} className={styles.variantRow}>
                            <select
                                className={styles.variantInput}
                                value={vr.size}
                                onChange={(e) => updateVariant(i, { size: e.target.value })}
                            >
                                <option value="">Size…</option>
                                <option value="small">small</option>
                                <option value="medium">medium</option>
                                <option value="large">large</option>
                            </select>

                            <select
                                className={styles.variantInput}
                                value={vr.dough}
                                onChange={(e) => updateVariant(i, { dough: e.target.value })}
                            >
                                <option value="">Dough…</option>
                                <option value="thin">thin</option>
                                <option value="classic">classic</option>
                                <option value="wholegrain">wholegrain</option>
                            </select>

                            <input
                                className={styles.variantInput}
                                type="number"
                                min="0"
                                step="0.01"
                                placeholder="Extra price"
                                value={vr.extraPrice ?? 0}
                                onChange={(e) =>
                                    updateVariant(i, {
                                        extraPrice: Number.isNaN(parseFloat(e.target.value))
                                            ? 0
                                            : parseFloat(e.target.value),
                                    })
                                }
                            />

                            <button
                                type="button"
                                className={`${styles.btn} ${styles.btnDanger}`}
                                onClick={() => removeVariant(i)}
                            >
                                Remove
                            </button>
                        </div>
                    ))}

                    <div className={styles.row}>
                        <button type="button" className={styles.btn} onClick={addVariant}>
                            + Add variant
                        </button>
                    </div>
                </div>
            </div>

            {showImagePicker && (
                <div className={styles.field}>
                    <label>Image (optional)</label>
                    <input type="file" accept="image/*" onChange={(e) => onImagePicked?.(e.target.files?.[0] || null)} />
                </div>
            )}

            <div className={styles.row}>
                <button className={`${styles.btn} ${styles.btnPrimary}`} type="submit" disabled={busy || !canSave}>
                    {busy ? "Saving..." : "Save"}
                </button>
                <button className={styles.btn} type="button" onClick={onCancel} disabled={busy}>
                    Cancel
                </button>
            </div>
        </form>
    );
}
