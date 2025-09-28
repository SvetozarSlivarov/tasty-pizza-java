import React, { useEffect, useMemo, useState } from "react";
import {adminApi} from "../../api/admin";
import IngredientTable from "./components/IngredientTable";
import styles from "../../styles/Ingredients.module.css";

export default function IngredientsAdminPage() {
    const [rows, setRows] = useState([]);
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState(null);
    const [editingId, setEditingId] = useState(null);

    const [showDeleted, setShowDeleted] = useState(true);
    const [query, setQuery] = useState("");

    const load = async () => {
        setBusy(true);
        setError(null);
        try {
            const data = await adminApi.listIngredients();
            setRows(Array.isArray(data) ? data : data?.items || []);
        } catch (e) {
            setError(e?.message || "Failed to load ingredients");
        } finally {
            setBusy(false);
        }
    };

    useEffect(() => {
        load();
    }, []);

    const removeRow = async (id) => {
        if (!window.confirm("Delete ingredient?")) return;
        setBusy(true);
        setError(null);
        try {
            await adminApi.deleteIngredient(id);
            setRows((prev) =>
                prev.map((r) =>
                    r.id === id
                        ? { ...r, deleted: true, deletedAt: new Date().toISOString() }
                        : r
                )
            );
        } catch (e) {
            setError(e?.message || "Delete failed");
        } finally {
            setBusy(false);
        }
    };

    const restoreRow = async (id) => {
        setBusy(true);
        setError(null);
        try {
            await adminApi.restoreIngredient(id);
            setRows((prev) =>
                prev.map((r) =>
                    r.id === id ? { ...r, deleted: false, deletedAt: null } : r
                )
            );
        } catch (e) {
            setError(e?.message || "Restore failed");
        } finally {
            setBusy(false);
        }
    };

    const onEdit = (id) => {
        setEditingId(id);
    };

    const filtered = useMemo(() => {
        let list = rows;
        if (!showDeleted) list = list.filter((r) => !r.deleted);
        if (query.trim()) {
            const q = query.trim().toLowerCase();
            list = list.filter(
                (r) =>
                    String(r.id).includes(q) ||
                    (r.name || "").toLowerCase().includes(q) ||
                    (r.type?.name || "").toLowerCase().includes(q)
            );
        }
        return list;
    }, [rows, showDeleted, query]);

    return (
        <div className={styles.wrap}>
            <div className={styles.header}>
                <h1 className={styles.title}>Ingredients (Admin)</h1>
                <div className={styles.toolbar}>
                    <input
                        className={styles.input}
                        placeholder="Search by id, name or type..."
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                    <label className={styles.checkboxLabel}>
                        <input
                            type="checkbox"
                            checked={showDeleted}
                            onChange={(e) => setShowDeleted(e.target.checked)}
                        />
                        <span>Show deleted</span>
                    </label>
                    <button className={styles.btn} onClick={load} disabled={busy}>
                        Refresh
                    </button>
                </div>
            </div>

            {error && <div className={styles.error}>{error}</div>}

            <IngredientTable
                rows={filtered}
                busy={busy}
                onEdit={onEdit}
                onDelete={removeRow}
                onRestore={restoreRow}
            />
        </div>
    );
}