import { Link } from "react-router-dom";
import "../styles/home.css";

export default function Home() {
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

            <section className="menu-preview">
                <div className="head">
                    <h2>Best sellers</h2>
                    <Link to="/menu" className="btn link">See full menu →</Link>
                </div>
                <div className="cards">
                    {[
                        { name: "Margherita", price: "9.90", emoji: "🍅" },
                        { name: "Pepperoni", price: "12.90", emoji: "🌶️" },
                        { name: "Four Cheese", price: "13.90", emoji: "🧀" },
                    ].map((p) => (
                        <article className="card" key={p.name}>
                            <div className="pic">{p.emoji}</div>
                            <h4>{p.name}</h4>
                            <p className="price">{p.price} BGN</p>
                            <Link to="/menu" className="btn small">Add</Link>
                        </article>
                    ))}
                </div>
            </section>

            <section className="testimonials">
                <h2>What our customers say</h2>
                <div className="quotes">
                    <blockquote>
                        “Best crust in town!” <cite>— Mira</cite>
                    </blockquote>
                    <blockquote>
                        “Arrived in 22 minutes. Hot and juicy.” <cite>— Ivan</cite>
                    </blockquote>
                    <blockquote>
                        “My favorite Pepperoni. Perfect balance.” <cite>— George</cite>
                    </blockquote>
                </div>
            </section>
        </div>
    );
}
