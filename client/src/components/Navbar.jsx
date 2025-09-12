import {useEffect, useState} from "react";
import { useAuth } from "../context/AuthContext";
import { FaShoppingCart } from "react-icons/fa";
import {NavLink, Link, useLocation, useNavigate} from "react-router-dom";
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
    const { user, logout } = useAuth();

    const loc = useLocation();
    const nav = useNavigate();
    const closeMenu = () => setOpen(false);

    useEffect(() => { setOpen(false); }, [loc.pathname]);
    useEffect(() => {
        const onKey = (e) => e.key === "Escape" && setOpen(false);
        document.addEventListener("keydown", onKey);
        document.body.style.overflow = open ? "hidden" : "";
        return () => { document.removeEventListener("keydown", onKey); document.body.style.overflow = ""; };
    }, [open]);

    const isAdmin =
        Array.isArray(user?.roles) ? user.roles.includes("ADMIN") :
            (user?.role === "ADMIN" || user?.role === "Admin");

    const navClass = ({ isActive }) => (isActive ? "active" : undefined);

    const handleSignOut = async () => {
        await logout();
        setOpen(false);
        nav("/", { replace: true });
    };

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
                        {!user ? (
                            <NavLink to="/login" className={navClass}>Sign in</NavLink>
                        ) : (
                            <>
                                <NavLink to="/profile" className={navClass}>Profile</NavLink>
                                {isAdmin && <NavLink to="/admin" className={navClass}>Admin</NavLink>}
                                <button className="linklike" type="button" onClick={handleSignOut}>Sign out</button>
                            </>
                        )}
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
