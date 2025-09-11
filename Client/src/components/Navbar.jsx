import { useState } from "react";
import { NavLink, Link } from "react-router-dom";
import "../styles/navbar.css";

// мини SVG-и за икони (без библиотеки)
const CartIcon = () => (
    <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden>
        <path d="M7 4h-2l-1 2h2l3.6 7.59-1.35 2.44A2 2 0 0 0 10 19h9v-2h-9l1.1-2h7.45a2 2 0 0 0 1.8-1.11l3.24-6.49A1 1 0 0 0 23 4h-3" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round"/>
        <circle cx="10.5" cy="20.5" r="1.5" fill="currentColor"/>
        <circle cx="18.5" cy="20.5" r="1.5" fill="currentColor"/>
    </svg>
);

const Burger = ({ open }) => (
    <div className={`burger ${open ? "open" : ""}`} aria-hidden>
        <span></span><span></span><span></span>
    </div>
);

export default function Navbar() {
    const [open, setOpen] = useState(false);

    // TODO: вържи това към реалния state на количката
    const cartCount = 2;

    const closeMenu = () => setOpen(false);

    return (
        <>
            <header className="nav-wrap">
                <div className="nav">
                    <Link to="/" className="brand" onClick={closeMenu}>
                        {/* използвай същата пица от favicon като кръгче */}
                        <div className="logo">🍕</div>
                        <span>Tasty <b>Pizza</b></span>
                    </Link>

                    <nav className={`links ${open ? "show" : ""}`}>
                        <NavLink to="/" end onClick={closeMenu}>Home</NavLink>
                        <NavLink to="/menu" onClick={closeMenu}>Menu</NavLink>
                        <NavLink to="/login" onClick={closeMenu}>Login</NavLink>
                        <NavLink to="/cart" className="cart" onClick={closeMenu}>
                            <CartIcon />
                            <span>Cart</span>
                            {cartCount > 0 && <em className="badge">{cartCount}</em>}
                        </NavLink>
                    </nav>

                    <button
                        className="hamburger"
                        aria-label="Toggle menu"
                        aria-expanded={open}
                        onClick={() => setOpen(v => !v)}
                    >
                        <Burger open={open} />
                    </button>
                </div>
            </header>

            {/* spacer за фиксирания хедър */}
            <div style={{ height: "76px" }} />
        </>
    );
}
