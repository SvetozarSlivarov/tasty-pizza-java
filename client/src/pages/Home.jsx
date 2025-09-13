// src/pages/Home.jsx
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "../styles/home.css";
import { catalogApi } from "../api/catalog";

const FallbackImg = "images/fallBackImg.png";

export default function Home() {
    const [latestPizzas, setLatestPizzas] = useState([]);
    const [latestDrinks, setLatestDrinks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState(null);

    useEffect(() => {
        let mounted = true;
        (async () => {
            try {
                setLoading(true);
                const [pz, dr] = await Promise.all([
                    catalogApi.pizzas(true),   // взимаме налични пици
                    catalogApi.drinks(true),   // и налични напитки
                ]);

                if (!mounted) return;

                const byIdDesc = (a, b) => (b?.id ?? 0) - (a?.id ?? 0);
                setLatestPizzas((pz ?? []).slice().sort(byIdDesc).slice(0, 3));
                setLatestDrinks((dr ?? []).slice().sort(byIdDesc).slice(0, 3));
            } catch (e) {
                console.error(e);
                setErr(e?.message || "Неуспешно зареждане");
            } finally {
                if (mounted) setLoading(false);
            }
        })();
        return () => { mounted = false; };
    }, []);

    return (
        <div className="home">
            {/* HERO */}
            <section className="hero">
                <div className="hero-content">
                    <p className="eyebrow">Fresh • Fast • Hot</p>
                    <h1>Tasty <span>Pizza</span> in minutes</h1>
                    <p className="sub">
                        Hand-tossed dough, premium ingredients, stone-baked perfection.
                        Order now and get your pizza in under 30 minutes.
                    </p>
                    <div className="cta-row">
                        <Link to="/menu" className="btn primary">Order now</Link>
                        <a href="#why" className="btn ghost">Learn more</a>
                    </div>
                </div>
            </section>

            {/* WHY */}
            <section id="why" className="why">
                <h2>Why choose <span>Tasty Pizza</span>?</h2>
                <div className="why-grid">
                    <article>
                        <div className="ic">🔥</div>
                        <h3>Stone-baked</h3>
                        <p>That perfect crust — crisp outside, soft inside.</p>
                    </article>
                    <article>
                        <div className="ic">🧀</div>
                        <h3>Real mozzarella</h3>
                        <p>Stretchy, fragrant, and full of flavor.</p>
                    </article>
                    <article>
                        <div className="ic">⏱️</div>
                        <h3>Fast delivery</h3>
                        <p>Average delivery time under 30 minutes.</p>
                    </article>
                </div>
            </section>

            {/* NEWEST PIZZAS */}
            <section className="section">
                <div className="container">
                    <h2 className="section-title">NEWEST PIZZAS</h2>

                    {loading && (
                        <div className="skeleton-grid">
                            {Array.from({ length: 3 }).map((_, i) => <div className="skeleton-card" key={i} />)}
                        </div>
                    )}

                    {err && <p className="error">{err}</p>}

                    {!loading && !err && (
                        <div className="grid">
                            {latestPizzas.map(p => (
                                <article className="card" key={p.id}>
                                    <div className="thumb">
                                        <img src={p.imageUrl || FallbackImg} alt={p.name} loading="lazy" />
                                    </div>
                                    <div className="body">
                                        <h3 className="title">{p.name}</h3>
                                        {p.description && <p className="desc">{p.description}</p>}
                                        <div className="meta">
                                            <span className="price">from {Number(p.basePrice ?? p.price).toFixed(2)} EUR</span>
                                            {p.spicyLevel && <span className="badge">{p.spicyLevel}</span>}
                                        </div>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}
                </div>
            </section>

            {/* NEWEST DRINKS */}
            <section className="section">
                <div className="container">
                    <h2 className="section-title">NEWEST DRINKS</h2>

                    {loading && (
                        <div className="skeleton-grid">
                            {Array.from({ length: 3 }).map((_, i) => <div className="skeleton-card" key={i} />)}
                        </div>
                    )}

                    {!loading && !err && (
                        <div className="grid drinks">
                            {latestDrinks.map(d => (
                                <article className="card" key={d.id}>
                                    <div className="thumb">
                                        <img src={d.imageUrl || FallbackImg} alt={d.name} loading="lazy" />
                                    </div>
                                    <div className="body">
                                        <h3 className="title">{d.name}</h3>
                                        {d.description && <p className="desc">{d.description}</p>}
                                        <div className="meta">
                                            <span className="price">{Number(d.price).toFixed(2)} EUR</span>
                                        </div>
                                    </div>
                                </article>
                            ))}
                        </div>
                    )}

                    <div className="more-row">
                        <Link className="btn link" to="/menu">See all →</Link>
                    </div>
                </div>
            </section>

            <section className="testimonials">
                <h2>What our customers say</h2>
                <div className="quotes">
                    <blockquote>“Best crust in town!” <cite>— Mira</cite></blockquote>
                    <blockquote>“Arrived in 22 minutes. Hot and juicy.” <cite>— Ivan</cite></blockquote>
                    <blockquote>“My favorite Pepperoni. Perfect balance.” <cite>— George</cite></blockquote>
                </div>
            </section>
        </div>
    );
}
