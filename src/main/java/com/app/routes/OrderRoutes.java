package com.app.routes;

import com.app.controller.OrderController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class OrderRoutes {

    private final OrderController orderController;

    public OrderRoutes(OrderController orderController) {
        this.orderController = orderController;
    }

    public void routes(Javalin app) {

        // Middleware for role checking
        app.before("/api/orders/*", context -> RoleMiddleware.allow(context, Role.ADMIN));

        // Routes
        app.post("/api/orders", orderController::createOrder);
        app.get("/api/orders", orderController::getAllOrders);
        app.get("/api/orders/{public_id}", orderController::getOrderById);
        app.patch("/api/orders/{public_id}/status", orderController::updateOrderStatus);
    }
}
