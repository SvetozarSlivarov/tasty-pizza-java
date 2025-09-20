package com.example.config;

import com.example.dao.OrderDao;
import com.example.dao.impl.OrderDaoImpl;
import io.github.cdimascio.dotenv.Dotenv;
import com.example.controller.*;
import com.example.security.JwtService;
import com.example.service.*;
import com.example.storage.CloudinaryStorageService;
import com.example.storage.StorageService;

public class Beans {
    public final OrderDao orderDao;

    public final JwtService jwt;
    public final UserService users;
    public final PizzaService pizzas;
    public final IngredientService ingredients;
    public final PizzaIngredientService pizzaIngredient;
    public final DrinkService drinks;
    public final CartService cartService;

    public final AuthController authCtl;
    public final UserController userCtl;
    public final PizzaController pizzaCtl;
    public final IngredientController ingredientCtl;
    public final IngredientTypeController ingredientTypeCtl;
    public final DrinkController drinkCtl;
    public final CartController cartCtl;
    public final OrderController orderCtl;
    public final MaintenanceController maintenanceController;

    private final StorageService storageService;

    public Beans(ServerConfig cfg) {
        // 1) JWT
        this.jwt = new JwtService(cfg.jwtBase64Secret(), cfg.jwtTtlSeconds());

        Dotenv dotenv = tryLoadDotenv();

        String cloud = firstNonEmpty(
                System.getenv("CLOUDINARY_CLOUD_NAME"),
                dotenv != null ? dotenv.get("CLOUDINARY_CLOUD_NAME") : null
        );
        String key   = firstNonEmpty(
                System.getenv("CLOUDINARY_API_KEY"),
                dotenv != null ? dotenv.get("CLOUDINARY_API_KEY") : null
        );
        String sec   = firstNonEmpty(
                System.getenv("CLOUDINARY_API_SECRET"),
                dotenv != null ? dotenv.get("CLOUDINARY_API_SECRET") : null
        );

        if (cloud == null || key == null || sec == null) {
            throw new IllegalStateException("""
                Cloudinary credentials missing.
                Expected CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET
                (set via Run Configuration Environment Variables или в .env).
            """);
        }

        // 3) Storage
        this.storageService = new CloudinaryStorageService(cloud, key, sec);

        this.orderDao = new OrderDaoImpl();

        // 4) Services
        this.users           = new UserService();
        this.ingredients     = new IngredientService();
        this.pizzaIngredient = new PizzaIngredientService();
        this.cartService     = new CartService();

        this.pizzas = new PizzaService(storageService);
        this.drinks = new DrinkService(storageService);

        // 5) Controllers
        this.authCtl           = new AuthController(users, jwt);
        this.userCtl           = new UserController(users, orderDao,jwt, cartService);
        this.pizzaCtl          = new PizzaController(pizzas, pizzaIngredient, jwt);
        this.ingredientCtl     = new IngredientController(ingredients, jwt);
        this.ingredientTypeCtl = new IngredientTypeController(ingredients, jwt);
        this.drinkCtl          = new DrinkController(drinks, jwt);
        this.cartCtl           = new CartController(cartService, jwt);
        this.orderCtl          = new OrderController(cartService, jwt, orderDao);
        this.maintenanceController = new MaintenanceController(jwt);
    }

    private static Dotenv tryLoadDotenv() {
        try {
            return Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e1) {
            try {
                return Dotenv.configure()
                        .ignoreIfMissing()
                        .directory("./server")
                        .load();
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private static String firstNonEmpty(String a, String b) {
        return (a != null && !a.isBlank()) ? a : ((b != null && !b.isBlank()) ? b : null);
    }

    public StorageService storageService() { return storageService; }
}
