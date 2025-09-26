import React, { useState } from "react";
import styles from "../../../styles/Ingredients.module.css";

export default function TypeForm({ initial, onCancel, onSave, busy }) {
    const [name, setName] = useState(initial?.name ?? "");
    const canSave = name.trim().length >= 2;

    const submit = (e) => {
        e.preventDefault();
        if (!canSave || busy) return;
        onSave({ name: name.trim() });
    };

    return (
        <form className={styles.panel} onSubmit={submit}>
            <div className={styles.row}>
                <label className={styles.label}>Name</label>
                <input
                    className={styles.input}
                    placeholder="e.g. cheese, meat, sauce"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    disabled={busy}
                />
            </div>
            <div className={styles.row} style={{ gap: 8 }}>
                <button className={`${styles.btn} ${styles.btnPrimary}`} type="submit" disabled={!canSave || busy}>
                    Save
                </button>
                <button className={styles.btn} type="button" onClick={onCancel} disabled={busy}>
                    Cancel
                </button>
            </div>
        </form>
    );
}
