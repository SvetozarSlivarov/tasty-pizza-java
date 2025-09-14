import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Menu from "./pages/Menu";
import Cart from "./pages/Cart";
import Login from "./pages/Login";
import PizzaDetails from "./pages/PizzaDetails";
import DrinkDetails from "./pages/DrinkDetails";
import {AuthProvider} from "./context/AuthContext";
import Register from "./pages/Register";

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Navbar />
                <main style={{ maxWidth: 1100, margin: "0 auto", padding: "24px 16px" }}>
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/menu" element={<Menu />} />
                        <Route path="/cart" element={<Cart />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/pizza/:id" element={<PizzaDetails />} />
                        <Route path="/drink/:id" element={<DrinkDetails />} />
                    </Routes>
                </main>
                < Footer />
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
