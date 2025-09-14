package com.example.utils;

import com.example.dao.OrderDao;
import com.example.dao.impl.OrderDaoImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class CartJanitor implements AutoCloseable {
    private final ScheduledExecutorService exec =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "cart-janitor");
                t.setDaemon(true);
                return t;
            });

    private final OrderDao orderDao;
    private final Duration ttl;
    private final Duration period;
    private final Duration initialDelay;

    public CartJanitor(OrderDao orderDao, Duration ttl, Duration period, Duration initialDelay) {
        this.orderDao = orderDao;
        this.ttl = ttl;
        this.period = period;
        this.initialDelay = initialDelay;
    }

    public static CartJanitor defaultInstance() {
        return new CartJanitor(new OrderDaoImpl(),
                Duration.ofDays(30), Duration.ofHours(24), Duration.ofHours(1));
    }

    public void start() {
        exec.scheduleAtFixedRate(this::runSafe,
                initialDelay.toSeconds(),
                period.toSeconds(),
                TimeUnit.SECONDS);
    }

    private void runSafe() {
        try {
            Instant cutoff = Instant.now().minus(ttl);
            int deleted = orderDao.deleteGuestCartsOlderThan(cutoff);
            System.out.println("[CartJanitor] Deleted guest carts: " + deleted);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    @Override
    public void close() {
        exec.shutdownNow();
    }
}
