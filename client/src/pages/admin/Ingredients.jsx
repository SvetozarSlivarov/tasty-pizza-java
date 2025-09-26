import React, { useEffect, useMemo, useState } from "react";
import { adminApi } from "../../api/admin";
import styles from "../../styles/Ingredients.module.css";
import IngredientForm from "./components/IngredientForm";
import IngredientToolbar from "./components/IngredientToolbar";
import IngredientTable from "./components/IngredientTable";
import Pagination from "./components/Pagination";

export default function Ingredients() {
    const [rows, setRows] = useState([]);
    const [types, setTypes] = useState([]);
    const [error, setError] = useState(null);
    const [busy, setBusy] = useState(false);

    // UI state
    const [creating, setCreating] = useState(false);
    const [editingId, setEditingId] = useState(null);

    // Filters/sort/search/pagination
    const [search, setSearch] = useState("");
    const [typeFilterId, setTypeFilterId] = useState("");
    const [sortKey, setSortKey] = useState("name");
    const [sortDir, setSortDir] = useState("asc");
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);

    const refresh = async () => {
        setBusy(true);
        setError(null);
        try {
            const [ingredients, ingredientTypes] = await Promise.all([
                adminApi.listIngredients(),
                adminApi.listIngredientTypes(),
            ]);
            setRows(ingredients);
            setTypes(ingredientTypes);
        } catch (e) {
            setError(e?.message || "Failed to load");
        } finally {
            setBusy(false);
        }
    };

    useEffect(() => { refresh(); }, []);

    // CREATE TYPE (inline)
    const handleCreateType = async (name) => {
        const created = await adminApi.createIngredientType({ name });
        setTypes((prev) => (prev.some(t => t.id === created.id) ? prev : [...prev, created]));
        return created;
    };

    // CREATE
    const saveCreate = async (payload) => {
        setBusy(true); setError(null);
        try {
            const created = await adminApi.createIngredient(payload);
            setRows(prev => [created, ...prev]);
            setCreating(false);
            setPage(1);
        } catch (e) {
            setError(e?.message || "Create failed");
        } finally {
            setBusy(false);
        }
    };

    // UPDATE
    const saveEdit = async (id, payload) => {
        setBusy(true); setError(null);
        try {
            const updated = await adminApi.updateIngredient(id, payload);
            setRows(prev => prev.map(r => (r.id === id ? updated : r)));
            setEditingId(null);
        } catch (e) {
            setError(e?.message || "Update failed");
        } finally {
            setBusy(false);
        }
    };

    // DELETE
    const removeRow = async (id) => {
        if (!window.confirm("Delete ingredient?")) return;
        setBusy(true); setError(null);
        try {
            await adminApi.deleteIngredient(id);
            setRows(prev => prev.filter(r => r.id !== id));
            setPage(p => {
                const totalAfter = filteredSorted.length - 1;
                const totalPagesAfter = Math.max(1, Math.ceil(totalAfter / pageSize));
                return Math.min(p, totalPagesAfter);
            });
        } catch (e) {
            setError(e?.message || "Delete failed");
        } finally {
            setBusy(false);
        }
    };

    const filteredSorted = useMemo(() => {
        const s = search.trim().toLowerCase();
        const tf = typeFilterId ? Number(typeFilterId) : null;

        let list = rows;

        if (tf) list = list.filter(r => r.type?.id === tf);

        if (s) {
            list = list.filter(r =>
                r.name?.toLowerCase().includes(s) ||
                r.type?.name?.toLowerCase().includes(s)
            );
        }

        const dir = sortDir === "asc" ? 1 : -1;
        const collator = new Intl.Collator(undefined, { sensitivity: "base", numeric: true });
        list = [...list].sort((a, b) => {
            let va, vb;
            switch (sortKey) {
                case "id":   va = a.id; vb = b.id; return (va - vb) * dir;
                case "type": va = a.type?.name || ""; vb = b.type?.name || ""; return collator.compare(va, vb) * dir;
                case "name":
                default:     va = a.name || ""; vb = b.name || ""; return collator.compare(va, vb) * dir;
            }
        });

        return list;
    }, [rows, search, typeFilterId, sortKey, sortDir]);

    const totalPages = Math.max(1, Math.ceil(filteredSorted.length / pageSize));
    const currentPage = Math.min(page, totalPages);
    const paged = useMemo(() => {
        const start = (currentPage - 1) * pageSize;
        return filteredSorted.slice(start, start + pageSize);
    }, [filteredSorted, currentPage, pageSize]);

    useEffect(() => { setPage(1); }, [search, typeFilterId]);

    return (
        <div className={styles.page}>
            <div className={styles.headerRow}>
                <h1 className={styles.title}>Ingredients</h1>
                <div className={styles.right} style={{ gap: 8 }}>
                    <button className={styles.btn} onClick={refresh} disabled={busy}>Refresh</button>
                    <button className={styles.btn} disabled={busy} onClick={() => setCreating(true)}>+ New Ingredient</button>
                </div>
            </div>

            <IngredientToolbar
                types={types}
                search={search}
                onSearch={setSearch}
                typeFilterId={typeFilterId}
                onTypeFilter={setTypeFilterId}
                sortKey={sortKey}
                sortDir={sortDir}
                onSortKey={setSortKey}
                onSortDir={setSortDir}
                pageSize={pageSize}
                onPageSize={setPageSize}
                disabled={busy}
            />

            {error && <div className={`${styles.panel} ${styles.error}`}>Error: {error}</div>}

            {creating && (
                <IngredientForm
                    types={types}
                    onCancel={() => setCreating(false)}
                    onSave={saveCreate}
                    onCreateType={handleCreateType}
                    busy={busy}
                />
            )}

            {editingId && (
                <IngredientForm
                    types={types}
                    initial={rows.find(r => r.id === editingId)}
                    onCancel={() => setEditingId(null)}
                    onSave={(payload) => saveEdit(editingId, payload)}
                    onCreateType={handleCreateType}
                    busy={busy}
                />
            )}

            <div className={styles.panel}>
                <div className={styles.tableWrap}>
                    <IngredientTable
                        rows={paged}
                        busy={busy}
                        onEdit={(id) => setEditingId(id)}
                        onDelete={removeRow}
                    />
                </div>
                <Pagination
                    page={currentPage}
                    totalPages={totalPages}
                    onPage={setPage}
                />
            </div>
        </div>
    );
}
