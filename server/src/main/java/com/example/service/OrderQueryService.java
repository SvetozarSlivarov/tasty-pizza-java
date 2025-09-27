package com.example.service;

import com.example.dao.*;
import com.example.dto.order.*;
import com.example.model.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class OrderQueryService {
    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;
    private final OrderItemCustomizationDao customizationDao;
    private final ProductDao productDao;
    private final PizzaDao pizzaDao;
    private final DrinkDao drinkDao;
    private final PizzaVariantDao pizzaVariantDao;
    private final IngredientDao ingredientDao;
    private final UserService userService;

    public OrderQueryService(
            OrderDao orderDao,
            OrderItemDao orderItemDao,
            OrderItemCustomizationDao customizationDao,
            ProductDao productDao,
            PizzaDao pizzaDao,
            DrinkDao drinkDao,
            PizzaVariantDao pizzaVariantDao,
            IngredientDao ingredientDao,
            UserService userService
    ) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.customizationDao = customizationDao;
        this.productDao = productDao;
        this.pizzaDao = pizzaDao;
        this.drinkDao = drinkDao;
        this.pizzaVariantDao = pizzaVariantDao;
        this.ingredientDao = ingredientDao;
        this.userService = userService;
    }

    // ---------- helpers ----------
    private static String orderNumber(Order o) {
        Timestamp ts = o.getOrderedAt();
        if (ts == null) return "#" + o.getId();
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.setTimeInMillis(ts.getTime());
        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH) + 1, d = c.get(Calendar.DAY_OF_MONTH);
        return String.format("#%04d%02d%02d-%05d", y, m, d, o.getId());
    }

    private String variantLabel(Integer variantId) {
        if (variantId == null) return null;
        PizzaVariant v = pizzaVariantDao.findById(variantId);
        if (v == null) return null;
        String size  = (v.getSize()  != null) ? v.getSize().name()  : null;
        String dough = (v.getDough() != null) ? v.getDough().name() : null;
        if (size != null && dough != null) return size + " • " + dough;
        if (size != null) return size;
        return dough;
    }

    private List<AdminCartCustomizationView> toAdminCustomizations(int orderItemId) {
        List<OrderCustomization> list = customizationDao.findByOrderItemId(orderItemId);
        List<AdminCartCustomizationView> out = new ArrayList<>(list.size());
        for (OrderCustomization oc : list) {
            Ingredient ing = oc.getIngredient();
            int ingId = (ing != null ? ing.getId() : -1);
            String name = (ing != null ? ing.getName() : "#" + ingId);
            out.add(new AdminCartCustomizationView(
                    ingId,
                    name,
                    oc.getAction().name().toLowerCase()
            ));
        }
        return out;
    }

    private AdminOrderItemView toAdminItem(OrderItem it) {
        String type = productDao.findTypeById(it.getProductId());
        if (type == null) type = "unknown";

        String name = "#" + it.getProductId();
        String imageUrl = null;

        if ("pizza".equals(type)) {
            Pizza p = pizzaDao.findById(it.getProductId());
            if (p != null) { name = p.getName(); imageUrl = p.getImageUrl(); }
        } else if ("drink".equals(type)) {
            Drink d = drinkDao.findById(it.getProductId());
            if (d != null) { name = d.getName(); imageUrl = d.getImageUrl(); }
        }

        BigDecimal line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
        return new AdminOrderItemView(
                it.getId(),
                it.getProductId(),
                it.getPizzaVariantId(),
                it.getQuantity(),
                it.getUnitPrice(),
                line,
                it.getNote(),
                name,
                imageUrl,
                type,
                variantLabel(it.getPizzaVariantId()),
                toAdminCustomizations(it.getId())
        );
    }
    public List<AdminOrderListItemView> listOrders(String status, String q, Timestamp from, Timestamp to, int limit, int offset) {
        List<Order> orders = orderDao.search(status, q, from, to, "ordered_at_desc", limit, offset);

        List<AdminOrderListItemView> out = new ArrayList<>(orders.size());
        for (Order o : orders) {
            List<OrderItem> items = orderItemDao.findByOrderId(o.getId());
            int itemCount = items.stream().mapToInt(OrderItem::getQuantity).sum();
            BigDecimal total = items.stream()
                    .map(it -> it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String username = (o.getUserId() != null)
                    ? userService.findById(o.getUserId()).map(User::getUsername).orElse(null)
                    : null;

            out.add(new AdminOrderListItemView(
                    o.getId(),
                    orderNumber(o),
                    o.getStatus().name().toLowerCase(),
                    total,
                    itemCount,
                    o.getOrderedAt(),
                    username,
                    o.getDeliveryPhone(),
                    o.getDeliveryAddress()
            ));
        }
        return out;
    }

    public AdminOrderDetailView getOrder(int id) {
        Order o = orderDao.findById(id);
        if (o == null) return null;

        List<AdminOrderItemView> items = orderItemDao.findByOrderId(id).stream()
                .map(this::toAdminItem)
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(AdminOrderItemView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String username = (o.getUserId() != null)
                ? userService.findById(o.getUserId()).map(User::getUsername).orElse(null)
                : null;

        return new AdminOrderDetailView(
                o.getId(),
                orderNumber(o),
                o.getStatus().name().toLowerCase(),
                items,
                total,
                o.getOrderedAt(),
                o.getPreparingAt(),
                o.getOutForDeliveryAt(),
                o.getDeliveredAt(),
                o.getCancelledAt(),
                username,
                o.getDeliveryPhone(),
                o.getDeliveryAddress()
        );
    }
}
