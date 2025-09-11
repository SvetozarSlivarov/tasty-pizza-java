import { useState } from "react";
import { FaShoppingCart } from "react-icons/fa";
import { NavLink, Link } from "react-router-dom";
import "../styles/navbar.css";

const CartIcon = () => <FaShoppingCart size={20}/>

const Burger = ({ open }) => (
    <div className={`burger ${open ? "open" : ""}`} aria-hidden>
        <span></span><span></span><span></span>
    </div>
);

// src/components/Navbar.jsx
// ...imports stay the same

export default function Navbar() {
    const [open, setOpen] = useState(false);
    const closeMenu = () => setOpen(false);

    return (
        <>
            <header className="nav-wrap">
                <div className="nav">
                    <Link to="/" className="brand" onClick={closeMenu}>
                        <div className="logo">🍕</div>
                        <span>Tasty <b>Pizza</b></span>
                    </Link>

                    <nav className={`links ${open ? "show" : ""}`}>
                        <NavLink to="/" end onClick={closeMenu}>Home</NavLink>
                        <NavLink to="/menu" onClick={closeMenu}>Menu</NavLink>
                        <NavLink to="/login" onClick={closeMenu}>Sign in</NavLink>
                        <NavLink to="/cart" className="cart" onClick={closeMenu}>
                            <CartIcon />
                            <span>Cart</span>
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

            <div style={{ height: "76px" }} />
        </>
    );
}
