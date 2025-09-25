import { useEffect, useState } from "react";
import styles from "../../styles/Pizzas.module.css";
import { adminApi } from "../../api/admin";
import { fileToBase64 } from "../../utils/fileToBase64";
import DrinkForm, { normalizeDrink } from "./components/DrinkForm";

export default function DrinksAdmin() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState(null);

    const [creating, setCreating] = useState(false);
    const [editing, setEditing] = useState(null);

    const [imageBusy, setImageBusy] = useState(false);
    const [imageError, setImageError] = useState(null);

    async function load() {
        try {
            setLoading(true);
            setError(null);
            const list = await adminApi.listDrinks?.(true);
            setRows(Array.isArray(list) ? list : []);
        } catch (e) {
            setError(e?.message ?? String(e));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
    }, []);

    async function onCreate(payload, imageFile) {
        try {
            setBusy(true);
            setError(null);
            const created = await adminApi.createDrink(payload);
            if (imageFile) {
                try {
                    setImageBusy(true);
                    const dataBase64 = await fileToBase64(imageFile);
                    await adminApi.uploadDrinkImageBase64(created.id, {
                        filename: imageFile.name ?? "image.jpg",
                        contentType: imageFile.type || "image/jpeg",
                        dataBase64,
                    });
                } finally {
                    setImageBusy(false);
                }
            }
            setCreating(false);
            await load();
        } catch (e) {
            setError(e?.message ?? String(e));
        } finally {
            setBusy(false);
        }
    }

    async function onUpdate(id, payload, imageFile) {
        try {
            setBusy(true);
            setError(null);
            await adminApi.updateDrink(id, payload);
            if (imageFile) {
                try {
                    setImageBusy(true);
                    const dataBase64 = await fileToBase64(imageFile);
                    await adminApi.uploadDrinkImageBase64(id, {
                        filename: imageFile.name ?? "image.jpg",
                        contentType: imageFile.type || "image/jpeg",
                        dataBase64,
                    });
                } finally {
                    setImageBusy(false);
                }
            }
            setEditing(null);
            await load();
        } catch (e) {
            setError(e?.message ?? String(e));
        } finally {
            setBusy(false);
        }
    }

    async function onDelete(id) {
        if (!window.confirm("Delete this drink?")) return;
        try {
            setBusy(true);
            setError(null);
            await adminApi.deleteDrink(id);
            await load();
        } catch (e) {
            setError(e?.message ?? String(e));
        } finally {
            setBusy(false);
        }
    }

    async function onUploadImage(id, file) {
        if (!file) return;
        try {
            setImageBusy(true);
            setImageError(null);
            const dataBase64 = await fileToBase64(file);
            await adminApi.uploadDrinkImageBase64(id, {
                filename: file.name ?? "image.jpg",
                contentType: file.type || "image/jpeg",
                dataBase64,
            });
            await load();
        } catch (e) {
            setImageError(e?.message ?? String(e));
        } finally {
            setImageBusy(false);
        }
    }

    return (
        <div className={styles.wrap}>
            <h1 className={styles.h1}>Drinks (Admin)</h1>

            {error && <div className={`${styles.panel} ${styles.error}`}>{error}</div>}
            {imageError && (
                <div className={`${styles.panel} ${styles.error}`}>Image: {imageError}</div>
            )}

            <div className={styles.panel}>
                <div className={styles.row}>
                    <button
                        className={`${styles.btn} ${styles.btnPrimary}`}
                        onClick={() => setCreating(true)}
                        disabled={busy || loading}
                    >
                        + New drink
                    </button>
                    <button className={styles.btn} onClick={load} disabled={busy || loading}>
                        Reload
                    </button>
                </div>

                {creating && (
                    <DrinkForm
                        initial={normalizeDrink()}
                        onSubmit={(payload, img) => onCreate(payload, img)}
                        onCancel={() => setCreating(false)}
                    />
                )}
            </div>

            <div className={styles.panel}>
                <div className={styles.tableWrap}>
                    <table className={styles.table}>
                        <thead>
                        <tr>
                            <th className={styles.th}>ID</th>
                            <th className={styles.th}>Name</th>
                            <th className={styles.th}>Price</th>
                            <th className={styles.th}>Available</th>
                            <th className={styles.th}>Image</th>
                            <th className={styles.th}>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {loading ? (
                            <tr>
                                <td className={styles.td} colSpan={6}>
                                    Loading…
                                </td>
                            </tr>
                        ) : rows.length ? (
                            rows.map((r) => (
                                <tr key={r.id} className={!r.isAvailable ? styles.rowMuted : undefined}>
                                    <td className={styles.td}>{r.id}</td>
                                    <td className={styles.td}>
                                        {r.imageUrl ? (
                                            <img
                                                src={r.imageUrl}
                                                alt="drink"
                                                style={{ width: 48, height: 48, objectFit: "cover", borderRadius: 6 }}
                                            />
                                        ) : (
                                            <em>no image</em>
                                        )}
                                    </td>
                                    <td className={styles.td}>{r.name}</td>
                                    <td className={styles.td}>BGN {Number(r.price).toFixed(2)}</td>
                                    <td className={styles.td}>
                                        {r.isAvailable ? (
                                            <span className={styles.td}>✔</span>
                                        ) : (
                                            <span className={styles.td}>✖</span>
                                        )}
                                    </td>


                                    <td className={styles.td}>
                                        <div className={styles.row}>
                                            <button className={styles.btn} onClick={() => setEditing(r)}>
                                                Edit
                                            </button>
                                            <button
                                                className={`${styles.btn} ${styles.btnDanger}`}
                                                onClick={() => onDelete(r.id)}
                                                disabled={busy}
                                            >
                                                Delete
                                            </button>
                                        </div>
                                        <div>
                                            <input
                                                type="file"
                                                accept="image/*"
                                                disabled={imageBusy}
                                                onChange={(e) => onUploadImage(r.id, e.target.files?.[0] || null)}
                                            />
                                        </div>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td className={styles.td} colSpan={6}>
                                    No records.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {editing && (
                <div className={styles.panel}>
                    <h2 className={styles.h2}>Edit drink</h2>
                    <DrinkForm
                        initial={normalizeDrink(editing)}
                        onSubmit={(payload, img) => onUpdate(editing.id, payload, img)}
                        onCancel={() => setEditing(null)}
                    />
                </div>
            )}
        </div>
    );
}