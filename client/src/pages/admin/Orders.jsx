import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { adminOrdersApi, nextActionsForStatus } from "../../api/adminOrders";
import styles from "../../styles/adminOrders.module.css";

const StatusPill = ({ value }) => (
    <span className={styles.pill + " " + styles["st-" + String(value || "").toLowerCase()]}>{value}</span>
);

export default function AdminOrders() {
    const [params, setParams] = useSearchParams();
    const [rows, setRows] = useState([]);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const page = Number(params.get("page") || 1);
    const size = Number(params.get("size") || 20);
    const status = params.get("status") || "all";
    const q = params.get("q") || "";

    function updateParam(name, val) {
        const p = new URLSearchParams(params);
        if (val === null || val === undefined || val === "") p.delete(name);
        else p.set(name, String(val));
        setParams(p, { replace: true });
    }

    async function load() {
        setLoading(true); setError(null);
        try {
            const res = await adminOrdersApi.list({ status, q, page, size });
            setRows(res?.items ?? res?.data ?? res?.results ?? []);
            setTotal(res?.total ?? 0);
        } catch (e) {
            setError(e?.message ?? String(e));
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => { load(); /* eslint-disable-next-line */ }, [status, q, page, size]);

    const pageCount = Math.max(1, Math.ceil(total / size));

    return (
        <div className={styles.page}>
            <div className={styles.header}>
                <h1 className={styles.title}>Admin · Orders</h1>
            </div>

            <div className={styles.toolbar}>
                <label>
                    Status:&nbsp;
                    <select value={status} onChange={(e) => updateParam("status", e.target.value)}>
                        <option value="all">All</option>
                        <option value="ordered">ORDERED</option>
                        <option value="preparing">PREPARING</option>
                        <option value="out_for_delivery">OUT_FOR_DELIVERY</option>
                        <option value="delivered">DELIVERED</option>
                        <option value="cancelled">CANCELLED</option>
                    </select>
                </label>
                <input
                    type="search"
                    placeholder="Search (username, phone, address, order #)"
                    value={q}
                    onChange={(e) => updateParam("q", e.target.value)}
                    className={styles.search}
                />
            </div>

            {error && <div className={styles.error}>⚠ {error}</div>}
            {loading && <div className={styles.loading}>Loading…</div>}

            <div className={styles.card}>
                <div className={styles.body}>
                    <table className={styles.table}>
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>Status</th>
                            <th>Total</th>
                            <th>Items</th>
                            <th>Ordered at</th>
                            <th>Customer</th>
                            <th>Phone</th>
                            <th>Address</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {rows?.length === 0 && !loading && (
                            <tr><td colSpan="9" className={styles.empty}>No orders.</td></tr>
                        )}
                        {rows?.map((r) => (
                            <tr key={r.orderId}>
                                <td><Link to={`/admin/orders/${r.orderId}`}>{r.orderNumber ?? r.orderId}</Link></td>
                                <td><StatusPill value={r.status} /></td>
                                <td>{Number(r.total).toFixed(2)} лв</td>
                                <td>{r.itemCount}</td>
                                <td>{r.orderedAt ? new Date(r.orderedAt).toLocaleString() : "—"}</td>
                                <td>{r.customerUsername ?? "guest"}</td>
                                <td>{r.deliveryPhone ?? "—"}</td>
                                <td className={styles.ellipsis}>{r.deliveryAddress ?? "—"}</td>
                                <td>
                                    <RowActions row={r} onChanged={load} />
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>

                    <div className={styles.pagination}>
                        <button disabled={page <= 1} onClick={() => updateParam("page", page - 1)}>Prev</button>
                        <span>Page {page} / {pageCount}</span>
                        <button disabled={page >= pageCount} onClick={() => updateParam("page", page + 1)}>Next</button>

                        <select value={size} onChange={(e) => updateParam("size", Number(e.target.value))}>
                            <option value="10">10</option>
                            <option value="20">20</option>
                            <option value="50">50</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    );
}

function RowActions({ row, onChanged }) {
    const [busy, setBusy] = useState(false);
    async function run(action) {
        setBusy(true);
        try {
            await adminOrdersApi[action](row.orderId);
            await onChanged();
        } catch (e) {
            alert(e?.message ?? String(e));
        } finally {
            setBusy(false);
        }
    }
    const actions = nextActionsForStatus(row.status);
    if (actions.length === 0) return <span className={styles.muted}>—</span>;
    return (
        <div className={styles.rowActions}>
            {actions.map((a) => (
                <button key={a} disabled={busy} onClick={() => run(a)}>{label(a)}</button>
            ))}
        </div>
    );
}

function label(action) {
    switch (action) {
        case "startPreparing": return "Start preparing";
        case "outForDelivery": return "Out for delivery";
        case "deliver":        return "Deliver";
        case "cancel":         return "Cancel";
        default: return action;
    }
}
