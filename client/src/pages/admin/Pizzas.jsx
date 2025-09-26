import { useEffect, useState } from "react";
import styles from "../../styles/Pizzas.module.css";
import { adminApi } from "../../api/admin";
import { fileToBase64 } from "../../utils/fileToBase64";
import PizzaForm, { normalizePizza } from "./components/PizzaForm";
import PizzaIngredientsEditor from "./components/PizzaIngredientsEditor";

export default function PizzasAdmin() {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(false);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState(null);

    const [creating, setCreating] = useState(false);
    const [editing, setEditing] = useState(null);
    const [imageBusy, setImageBusy] = useState(false);
    const [imageError, setImageError] = useState(null);
    const [createImageFile, setCreateImageFile] = useState(null);

    const [showIngredientsFor, setShowIngredientsFor] = useState(null);

    const load = async () => {
        setLoading(true);
        setError(null);
        try {
            const list = await adminApi.listPizzas(false, true);
            setRows(Array.isArray(list) ? list : []);
        } catch (e) {
            setError(e?.message || "Failed to load pizzas");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    // CREATE
    const handleCreate = async (payload) => {
        setBusy(true);
        try {
            const created = await adminApi.createPizza(payload);
            const id = created?.id;

            if (id && createImageFile) {
                const { filename, contentType, base64 } = await fileToBase64(createImageFile);
                await adminApi.uploadPizzaImageBase64(id, {
                    filename,
                    contentType,
                    dataBase64: base64,
                });
            }

            setCreating(false);
            setCreateImageFile(null);
            setShowIngredientsFor(id);
            setRows(prev => [created, ...prev]);
        } catch (e) {
            alert(e?.message || "Create failed");
        } finally {
            setBusy(false);
        }
    };

    const handleUpdate = async (id, payload) => {
        setBusy(true);
        try {
            await adminApi.updatePizza(id, payload);
            setEditing(null);
            await load();
        } catch (e) {
            alert(e?.message || "Update failed");
        } finally {
            setBusy(false);
        }
    };

    // DELETE
    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this pizza?")) return;
        setBusy(true);
        try {
            await adminApi.deletePizza(id);
            await load();
        } catch (e) {
            alert(e?.message || "Delete failed");
        } finally {
            setBusy(false);
        }
    };

    // Upload image for existing pizza (Base64 → Backend)
    const handleUploadBase64 = async (pizza) => {
        setImageError(null);
        const input = document.createElement("input");
        input.type = "file";
        input.accept = "image/*";
        input.onchange = async () => {
            const file = input.files?.[0];
            if (!file) return;
            setImageBusy(true);
            try {
                if (!/^image\//.test(file.type)) throw new Error("Only images are allowed");
                if (file.size > 5 * 1024 * 1024) throw new Error("Max size is 5MB");

                const { filename, contentType, base64 } = await fileToBase64(file);
                await adminApi.uploadPizzaImageBase64(pizza.id, {
                    filename,
                    contentType,
                    dataBase64: base64,
                });

                await load();
            } catch (e) {
                setImageError(e?.message || "Image upload failed");
            } finally {
                setImageBusy(false);
            }
        };
        input.click();
    };

    // Edit: fetch full pizza (with variants) before opening the form
    const onEditClick = async (row) => {
        try {
            const full = await adminApi.getPizza(row.id, true);
            setEditing({ id: full?.id ?? row.id, ...normalizePizza(full) });
        } catch (_) {
            setEditing({ id: row.id, ...normalizePizza(row) });
        }
    };

    return (
        <div className={styles.page}>
            <div className={styles.header}>
                <div className={styles.title}>Pizzas</div>
                <div className={styles.actions}>
                    <button
                        className={`${styles.btn} ${styles.btnPrimary}`}
                        onClick={() => setCreating(true)}
                        disabled={busy}
                    >
                        + New pizza
                    </button>
                </div>
            </div>

            {error && <div className={`${styles.panel} ${styles.error}`}>Error: {error}</div>}

            {creating && (
                <div className={styles.panel}>
                    <h3 className={styles.panelTitle}>Create pizza</h3>
                    <PizzaForm
                        onSubmit={handleCreate}
                        onCancel={() => { setCreating(false); setCreateImageFile(null); }}
                        busy={busy}
                        showImagePicker
                        onImagePicked={setCreateImageFile}
                    />
                </div>
            )}

            {showIngredientsFor && (
                <div className={styles.panel}>
                    <h3 className={styles.panelTitle}>Step 2: Ingredients for new pizza</h3>
                    <PizzaIngredientsEditor pizzaId={showIngredientsFor} />
                    <div className={styles.row} style={{ marginTop: 12 }}>
                        <button className={styles.btn} onClick={() => setShowIngredientsFor(null)}>
                            Finish
                        </button>
                    </div>
                </div>
            )}

            {editing && (
                <div className={styles.panel}>
                    <h3 className={styles.panelTitle}>Edit pizza</h3>
                    <PizzaForm
                        initial={editing}
                        onSubmit={(payload) => handleUpdate(editing.id, payload)}
                        onCancel={() => setEditing(null)}
                        busy={busy}
                    />
                    <div style={{ marginTop: 12 }}>
                        <PizzaIngredientsEditor pizzaId={editing.id} />
                    </div>
                </div>
            )}

            <div className={styles.tableWrap}>
                <table className={styles.table}>
                    <thead>
                    <tr>
                        <th className={styles.th}>ID</th>
                        <th className={styles.th}>Image</th>
                        <th className={styles.th}>Name</th>
                        <th className={styles.th}>Price</th>
                        <th className={styles.th}>Available</th>
                        <th className={styles.th}>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {loading ? (
                        <tr><td className={styles.td} colSpan={6}>Loading…</td></tr>
                    ) : rows?.length ? (
                        rows.map((r) => (
                            <tr key={r.id}>
                                <td className={styles.td}>{r.id}</td>
                                <td className={styles.td}>
                                    {r.imageUrl ? (
                                        <img className={styles.img} src={r.imageUrl} alt={r.name} />
                                    ) : (
                                        <span className={styles.note}>no image</span>
                                    )}
                                </td>
                                <td className={styles.td}>{r.name}</td>
                                <td className={styles.td}>
                                    {typeof r.basePrice === "number" ? r.basePrice.toFixed(2) : r.basePrice}
                                </td>
                                <td className={styles.td}>{r.isAvailable ? "✔" : "✖"}</td>
                                <td className={styles.td}>
                                    <div className={styles.row}>
                                        <button className={styles.btn} onClick={() => onEditClick(r)} disabled={busy}>Edit</button>
                                        <button className={`${styles.btn} ${styles.btnDanger}`} onClick={() => handleDelete(r.id)} disabled={busy}>Delete</button>
                                    </div>
                                    <div className={styles.row} style={{ marginTop: 8 }}>
                                        <button
                                            className={styles.btn}
                                            onClick={() => handleUploadBase64(r)}
                                            disabled={imageBusy}
                                            title="Upload image as Base64 to backend"
                                        >
                                            Upload (Base64 → Backend)
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr><td className={styles.td} colSpan={6}>No records.</td></tr>
                    )}
                    </tbody>
                </table>
            </div>

            {imageError && <div className={`${styles.panel} ${styles.error}`}>Image error: {imageError}</div>}
        </div>
    );
}
