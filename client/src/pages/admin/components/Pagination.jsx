import React from "react";
import styles from "../../../styles/Ingredients.module.css";

export default function Pagination({ page, totalPages, onPage }) {
    if (totalPages <= 1) return null;

    const go = (p) => onPage(Math.max(1, Math.min(totalPages, p)));

    const pages = [];
    const span = 2;
    const start = Math.max(1, page - span);
    const end   = Math.min(totalPages, page + span);
    for (let p = start; p <= end; p++) pages.push(p);

    return (
        <div className={styles.pagination}>
            <button className={styles.btn} onClick={() => go(1)} disabled={page === 1}>« First</button>
            <button className={styles.btn} onClick={() => go(page - 1)} disabled={page === 1}>‹ Prev</button>

            <div className={styles.pageNums}>
                {start > 1 && <span className={styles.ellipsis}>…</span>}
                {pages.map(p => (
                    <button
                        key={p}
                        className={`${styles.btn} ${p === page ? styles.pageActive : ""}`}
                        onClick={() => go(p)}
                    >
                        {p}
                    </button>
                ))}
                {end < totalPages && <span className={styles.ellipsis}>…</span>}
            </div>

            <button className={styles.btn} onClick={() => go(page + 1)} disabled={page === totalPages}>Next ›</button>
            <button className={styles.btn} onClick={() => go(totalPages)} disabled={page === totalPages}>Last »</button>
        </div>
    );
}
