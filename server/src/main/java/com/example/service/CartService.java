package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.dto.cart.CartCustomizationView;
import com.example.dto.cart.CartItemView;
import com.example.dto.cart.CartView;
import com.example.model.*;
import com.example.model.enums.CustomizationAction;
import com.example.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.*;

public class CartService {

    private final OrderDao orderDao = new OrderDaoImpl();
    private final OrderItemDao itemDao = new OrderItemDaoImpl();
    private final OrderItemCustomizationDao custDao = new OrderItemCustomizationDaoImpl();
    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final DrinkDao drinkDao = new DrinkDaoImpl();
    private final PizzaVariantDao variantDao = new PizzaVariantDaoImpl();
    private final ProductDao productDao = new ProductDaoImpl();
    private final PizzaIngredientDao pizzaIngredientDao = new PizzaIngredientDaoImpl();
    private final PizzaAllowedIngredientDao allowedDao = new PizzaAllowedIngredientDaoImpl();

    public int ensureCart(Integer userId, Integer cartIdHint) {
        if (cartIdHint != null) {
            var cookieCart = orderDao.findById(cartIdHint);
            if (cookieCart != null && cookieCart.getStatus() == OrderStatus.CART) {

                if (userId != null && userId > 0) {
                    Order userCart = null;
                    for (var o : orderDao.findByUserId(userId)) {
                        if (o.getStatus() == OrderStatus.CART) {
                            if (userCart == null || o.getId() > userCart.getId()) userCart = o;
                        }
                    }

                    if (userCart != null) {
                        if (cookieCart.getId() != userCart.getId()) {
                            mergeCartItems(cookieCart.getId(), userCart.getId());
                            deleteOrderCompletely(cookieCart.getId());
                        }
                        return userCart.getId();
                    } else {
                        if (cookieCart.getUserId() == null) {
                            cookieCart.setUserId(userId);
                            orderDao.update(cookieCart);
                        }
                        return cookieCart.getId();
                    }
                }

                return cookieCart.getId();
            }
        }

        if (userId != null && userId > 0) {
            for (var o : orderDao.findByUserId(userId)) {
                if (o.getStatus() == OrderStatus.CART) return o.getId();
            }
            var created = new Order();
            created.setUserId(userId);
            created.setStatus(OrderStatus.CART);
            orderDao.save(created);
            return created.getId();
        }

        var created = new Order();
        created.setUserId(null);
        created.setStatus(OrderStatus.CART);
        orderDao.save(created);
        return created.getId();
    }

    public CartView getCart(int orderId) {
        var o = orderDao.findById(orderId);
        if (o == null) throw new IllegalArgumentException("order_not_found");

        var rows  = itemDao.findByOrderId(orderId);
        var items = new ArrayList<CartItemView>();
        var total = BigDecimal.ZERO;

        for (var it : rows) {
            var custs = custDao.findByOrderItemId(it.getId());
            var cv = new ArrayList<CartCustomizationView>();
            for (var c : custs) {
                cv.add(new CartCustomizationView(
                        c.getIngredient().getId(),
                        c.getAction().name()
                ));
            }
            String prodType = java.util.Optional.ofNullable(
                    productDao.findTypeById(it.getProductId())
            ).orElse("pizza");

            String name;
            String imageUrl;
            String type;
            String variantLabel = null;

            if ("pizza".equalsIgnoreCase(prodType)) {
                var pizza = pizzaDao.findById(it.getProductId());
                name     = (pizza != null && pizza.getName() != null) ? pizza.getName() : "Pizza";
                imageUrl = (pizza != null) ? pizza.getImageUrl() : null;
                type     = "pizza";

                Integer vid = it.getPizzaVariantId();
                if (vid != null) {
                    var variant = variantDao.findById(vid.intValue());
                    if (variant != null) {
                        String size  = (variant.getSize()  != null) ? variant.getSize().name().toLowerCase()  : null;
                        String dough = (variant.getDough() != null) ? variant.getDough().name().toLowerCase() : null;
                        if (size != null && dough != null)       variantLabel = size + " · " + dough;
                        else if (size != null)                   variantLabel = size;
                        else if (dough != null)                  variantLabel = dough;
                    }
                }
            } else {
                var drink = drinkDao.findById(it.getProductId());
                name     = (drink != null && drink.getName() != null) ? drink.getName() : "Drink";
                imageUrl = (drink != null) ? drink.getImageUrl() : null;
                type     = "drink";
            }

            var line = it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));
            total = total.add(line);

            items.add(new CartItemView(
                    it.getId(),
                    it.getProductId(),
                    it.getPizzaVariantId(),
                    it.getQuantity(),
                    it.getUnitPrice(),
                    it.getNote(),
                    cv,
                    name,
                    imageUrl,
                    type,
                    variantLabel
            ));
        }

        return new CartView(
                o.getId(),
                o.getStatus().name().toLowerCase(),
                items,
                total
        );
    }

    public OrderItem addDrink(int orderId, int productId, int qty, String note) {
        if (qty <= 0) throw new IllegalArgumentException("qty_invalid");

        var d = drinkDao.findById(productId);
        if (d == null) throw new IllegalArgumentException("drink_not_found");

        var oi = baseItem(orderId, productId, null, qty, d.getPrice(), note);
        if (!itemDao.save(oi)) throw new IllegalStateException("create_failed");
        orderDao.touch(orderId);
        return oi;
    }

    public OrderItem addPizza(int orderId, int productId, Integer variantId, int qty, String note,
                              List<Integer> removeIds, List<Integer> addIds) {
        if (qty <= 0) throw new IllegalArgumentException("qty_invalid");

        var p = pizzaDao.findById(productId);
        if (p == null) throw new IllegalArgumentException("pizza_not_found");

        BigDecimal unit = p.getPrice();
        Integer toSet = null;

        if (variantId != null) {
            var v = variantDao.findById(variantId);
            if (v == null || v.getPizzaId() != productId) {
                throw new IllegalArgumentException("variant_invalid");
            }
            unit = unit.add(v.getExtraPrice());
            toSet = v.getId();
        }
        var toRemove = new LinkedHashSet<>(removeIds != null ? removeIds : java.util.List.<Integer>of());
        var toAdd    = new LinkedHashSet<>(addIds    != null ? addIds    : java.util.List.<Integer>of());

        for (Integer id : toRemove) {
            if (toAdd.contains(id)) {
                throw new IllegalArgumentException("ingredient_in_both_add_and_remove");
            }
        }
        if (!toRemove.isEmpty()) {
            var base = pizzaIngredientDao.findByPizzaId(productId);
            var removable = new HashMap<Integer, Boolean>();
            for (var l : base) removable.put(l.getIngredientId(), l.isRemovable());
            for (Integer ing : toRemove) {
                Boolean ok = removable.get(ing);
                if (ok == null) throw new IllegalArgumentException("remove_not_in_base");
                if (!ok) throw new IllegalArgumentException("remove_not_removable");
            }
        }

        if (!toAdd.isEmpty()) {
            var allowed = new HashSet<>(allowedDao.findIngredientIdsByPizzaId(productId));
            for (Integer ing : toAdd) {
                if (!allowed.contains(ing)) throw new IllegalArgumentException("add_not_allowed");
            }
        }

        var oi = baseItem(orderId, productId, toSet, qty, unit, note);
        if (!itemDao.save(oi)) throw new IllegalStateException("create_failed");

        if (!toRemove.isEmpty()) {
            for (Integer ing : toRemove) {
                custDao.add(new OrderCustomization(oi, ingredient(ing), com.example.model.enums.CustomizationAction.REMOVE));
            }
        }
        if (!toAdd.isEmpty()) {
            for (Integer ing : toAdd) {
                custDao.add(new OrderCustomization(oi, ingredient(ing), CustomizationAction.ADD));
            }
        }

        orderDao.touch(orderId);
        return oi;
    }
    public void replacePizzaCustomizations(int itemId, List<Integer> removeIds, List<Integer> addIds) {
        var it = itemDao.findById(itemId);
        if (it == null) throw new IllegalArgumentException("item_not_found");

        var p = pizzaDao.findById(it.getProductId());
        if (p == null) throw new IllegalArgumentException("not_pizza_item");

        var toRemove = new LinkedHashSet<>(removeIds != null ? removeIds : java.util.List.<Integer>of());
        var toAdd    = new java.util.LinkedHashSet<>(addIds    != null ? addIds    : java.util.List.<Integer>of());
        for (Integer id : toRemove) if (toAdd.contains(id)) throw new IllegalArgumentException("ingredient_in_both_add_and_remove");

        if (!toRemove.isEmpty()) {
            var base = pizzaIngredientDao.findByPizzaId(p.getId());
            var removable = new HashMap<Integer, Boolean>();
            for (var l : base) removable.put(l.getIngredientId(), l.isRemovable());
            for (Integer ing : toRemove) {
                Boolean ok = removable.get(ing);
                if (ok == null) throw new IllegalArgumentException("remove_not_in_base");
                if (!ok) throw new IllegalArgumentException("remove_not_removable");
            }
        }

        if (!toAdd.isEmpty()) {
            var allowed = new java.util.HashSet<>(allowedDao.findIngredientIdsByPizzaId(p.getId()));
            for (Integer ing : toAdd) {
                if (!allowed.contains(ing)) throw new IllegalArgumentException("add_not_allowed");
            }
        }

        var existing = custDao.findByOrderItemId(itemId);
        for (var c : existing) custDao.remove(c.getId());

        for (Integer ing : toRemove) custDao.add(new OrderCustomization(it, ingredient(ing), com.example.model.enums.CustomizationAction.REMOVE));
        for (Integer ing : toAdd)    custDao.add(new OrderCustomization(it, ingredient(ing), CustomizationAction.ADD));

        orderDao.touch(it.getOrder().getId());
    }

    public void setQuantity(int itemId, int qty) {
        var it = itemDao.findById(itemId);
        if (qty <= 0) throw new IllegalArgumentException("qty_invalid");
        if (!itemDao.updateQuantity(itemId, qty)) throw new IllegalStateException("update_failed");
        orderDao.touch(it.getOrder().getId());
    }

    public void setVariant(int itemId, Integer variantId) {
        var it = itemDao.findById(itemId);
        if (it == null) throw new IllegalArgumentException("item_not_found");

        var base = pizzaDao.findById(it.getProductId());
        if (base == null) throw new IllegalArgumentException("not_a_pizza");

        BigDecimal unit = base.getPrice();
        Integer toSet = null;

        if (variantId != null) {
            var v = variantDao.findById(variantId);
            if (v == null || v.getPizzaId() != it.getProductId()) {
                throw new IllegalArgumentException("variant_invalid");
            }
            unit = unit.add(v.getExtraPrice());
            toSet = v.getId();
        }

        if (!((OrderItemDaoImpl) itemDao).updateVariantAndPrice(itemId, toSet, unit)) {
            throw new IllegalStateException("update_failed");
        }
        orderDao.touch(it.getOrder().getId());
    }

    public void setNote(int itemId, String note) {
        var it = itemDao.findById(itemId);
        if (!((OrderItemDaoImpl) itemDao).updateNote(itemId, note)) {
            throw new IllegalStateException("update_failed");
        }
        orderDao.touch(it.getOrder().getId());
    }

    public void removeItem(int itemId) {
        var it = itemDao.findById(itemId);
        orderDao.touch(it.getOrder().getId());
        if (!itemDao.delete(itemId)) throw new IllegalStateException("delete_failed");
    }

    public void setDeliveryInfo(int orderId, String phone, String address) {
        if (!orderDao.setDeliveryInfo(orderId, phone, address)) {
            throw new IllegalStateException("cannot_set_delivery_info");
        }
        orderDao.touch(orderId);
    }

    public void checkout(int orderId) {
        var o = orderDao.findById(orderId);
        if (o == null) throw new IllegalArgumentException("order_not_found");
        if (o.getStatus() != OrderStatus.CART) throw new IllegalStateException("not_in_cart");

        if (!orderDao.updateStatus(orderId, "ordered"))
            throw new IllegalStateException("cannot_checkout");
        if (!orderDao.setOrderedNow(orderId))
            throw new IllegalStateException("cannot_set_ordered_at");
        orderDao.touch(orderId);
    }
    public void importFromOrder(int fromOrderId, int toCartOrderId) {
        var fromItems = itemDao.findByOrderId(fromOrderId);
        for (var it : fromItems) {
            if (it.getPizzaVariantId() != null) {
                var custs = custDao.findByOrderItemId(it.getId());
                var removeIds = new java.util.ArrayList<Integer>();
                var addIds    = new java.util.ArrayList<Integer>();
                for (var c : custs) {
                    switch (c.getAction()) {
                        case REMOVE -> removeIds.add(c.getIngredient().getId());
                        case ADD    -> addIds.add(c.getIngredient().getId());
                    }
                }
                addPizza(
                        toCartOrderId,
                        it.getProductId(),
                        it.getPizzaVariantId(),
                        it.getQuantity(),
                        it.getNote(),
                        removeIds,
                        addIds
                );
            } else {
                addDrink(toCartOrderId, it.getProductId(), it.getQuantity(), it.getNote());
            }
        }
        orderDao.touch(toCartOrderId);
    }


    public void startPreparing(int orderId) {
        transition(orderId, OrderStatus.ORDERED, OrderStatus.PREPARING, "cannot_start_preparing");
        if (!orderDao.setPreparingNow(orderId))
            throw new IllegalStateException("cannot_set_preparing_at");
    }

    public void outForDelivery(int orderId) {
        transition(orderId, OrderStatus.PREPARING, OrderStatus.OUT_FOR_DELIVERY, "cannot_out_for_delivery");
        if (!orderDao.setOutForDeliveryNow(orderId))
            throw new IllegalStateException("cannot_set_out_for_delivery_at");
    }

    public void deliver(int orderId) {
        transition(orderId, OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED, "cannot_deliver");
        if (!orderDao.setDeliveredNow(orderId))
            throw new IllegalStateException("cannot_set_delivered_at");
    }

    public void cancel(int orderId) {
        var o = orderDao.findById(orderId);
        if (o == null) throw new IllegalArgumentException("order_not_found");
        if (o.getStatus() == OrderStatus.DELIVERED) throw new IllegalStateException("cannot_cancel_delivered");
        if (!orderDao.updateStatus(orderId, "cancelled"))
            throw new IllegalStateException("cannot_cancel");
        if (!orderDao.setCancelledNow(orderId))
            throw new IllegalStateException("cannot_set_cancelled_at");
        orderDao.touch(orderId);
    }

    private void transition(int orderId, OrderStatus requiredCurrent, OrderStatus target, String err) {
        var o = orderDao.findById(orderId);
        if (o == null) throw new IllegalArgumentException("order_not_found");
        if (o.getStatus() != requiredCurrent) throw new IllegalStateException("invalid_status_transition");
        if (!orderDao.updateStatus(orderId, target.name().toLowerCase()))
            throw new IllegalStateException(err);
        orderDao.touch(orderId);
    }

    private static OrderItem baseItem(int orderId, int productId, Integer variantId,
                                      int qty, BigDecimal unit, String note) {
        Order o = new Order();
        o.setId(orderId);
        return new OrderItem(o, productId, variantId, qty, unit, note);
    }

    private static Ingredient ingredient(int id) {
        var i = new Ingredient();
        i.setId(id);
        return i;
    }
    private void mergeCartItems(int fromOrderId, int toOrderId) {
        if (fromOrderId == toOrderId) return;

        var fromRows = itemDao.findByOrderId(fromOrderId);
        for (var it : fromRows) {
            var copy = baseItem(
                    toOrderId,
                    it.getProductId(),
                    it.getPizzaVariantId(),
                    it.getQuantity(),
                    it.getUnitPrice(),
                    it.getNote()
            );
            itemDao.save(copy);
            try {
                itemDao.delete(it.getId());
            } catch (Exception ignored) {
            }
        }
    }
    private void deleteOrderCompletely(int orderId) {
        var rows = itemDao.findByOrderId(orderId);
        for (var it : rows) {
            var custs = custDao.findByOrderItemId(it.getId());
            for (var c : custs) {
                custDao.remove(c.getId());
            }
        }
        rows = itemDao.findByOrderId(orderId);
        for (var it : rows) {
            itemDao.delete(it.getId());
        }
        orderDao.delete(orderId);
    }
}
