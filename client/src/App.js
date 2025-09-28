import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Home from "./pages/Home";
import Menu from "./pages/Menu";
import Login from "./pages/Login";
import PizzaDetails from "./pages/PizzaDetails";
import DrinkDetails from "./pages/DrinkDetails";
import {AuthProvider} from "./context/AuthContext";
import Register from "./pages/Register";
import CartDrawer from "./components/CartDrawer";
import CartFab from "./components/CartFab";
import {CartProvider} from "./context/CartContext";
import Profile from "./pages/Profile";
import AdminHome from "./pages/admin/Home";
import PizzasAdmin from "./pages/admin/Pizzas";
import DrinksAdmin from "./pages/admin/Drinks";
import IngredientsAdmin from "./pages/admin/Ingredients";
import IngredientTypes from "./pages/admin/IngredientTypes";
import AdminOrders from "./pages/admin/Orders";
import AdminOrderDetails from "./pages/admin/OrderDetails";
import UsersAdmin from "./pages/admin/Users";

function App() {
    return (
        <CartProvider>
        <AuthProvider>
            <BrowserRouter>
                < Navbar />
                <main style={{ maxWidth: 1100, margin: "0 auto", padding: "24px 16px" }}>
                    <CartDrawer />
                    <CartFab />
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/menu" element={<Menu />} />
                        <Route path="/profile" element={<Profile />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/admin" element={<AdminHome />} />
                        <Route path="/admin/pizzas" element={<PizzasAdmin />} />
                        <Route path="/admin/drinks" element={<DrinksAdmin />}/>
                        <Route path="/admin/ingredients" element={<IngredientsAdmin />} />
                        <Route path="/admin/ingredient-types" element={<IngredientTypes />} />
                        <Route path="/admin/orders" element={<AdminOrders />} />
                        <Route path="/admin/orders/:id" element={<AdminOrderDetails />} />
                        <Route path="/admin/users" element={<UsersAdmin />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/pizza/:id" element={<PizzaDetails />} />
                        <Route path="/drink/:id" element={<DrinkDetails />} />
                    </Routes>
                </main>
                < Footer />
            </BrowserRouter>
        </AuthProvider>
        </CartProvider>
    );
}

export default App;
