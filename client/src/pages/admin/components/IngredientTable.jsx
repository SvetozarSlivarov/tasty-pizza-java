import React from "react";
import styles from "../../../styles/Ingredients.module.css";

export default function IngredientTable({ rows, busy, onEdit, onDelete }) {
    return (
        <table className={styles.table}>
            <thead>
            <tr>
                <th className={styles.th} style={{ width: 80 }}>ID</th>
                <th className={styles.th}>Name</th>
                <th className={styles.th} style={{ width: 260 }}>Type</th>
                <th className={styles.th} style={{ width: 220 }}>Actions</th>
            </tr>
            </thead>
            <tbody>
            {rows.length ? rows.map(row => (
                <tr key={row.id}>
                    <td className={styles.td}>{row.id}</td>
                    <td className={styles.td}>{row.name}</td>
                    <td className={styles.td}>{row.type ? `${row.type.name} (#${row.type.id})` : "—"}</td>
                    <td className={styles.td}>
                        <button className={styles.btn} disabled={busy} onClick={() => onEdit(row.id)}>Edit</button>
                        <button className={`${styles.btn} ${styles.btnDanger}`} disabled={busy} onClick={() => onDelete(row.id)}>Delete</button>
                    </td>
                </tr>
            )) : (
                <tr><td className={styles.td} colSpan={4}>No ingredients.</td></tr>
            )}
            </tbody>
        </table>
    );
}
