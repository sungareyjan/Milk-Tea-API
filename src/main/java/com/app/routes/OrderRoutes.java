package com.app.routes;

import com.app.controller.OrderController;
import io.javalin.Javalin;

public class OrderRoutes {

    private final OrderController orderController;

    public OrderRoutes(OrderController orderController) {
        this.orderController = orderController;
    }

    public void routes(Javalin app) {
        app.post("/api/orders", orderController::create);
        app.get("/api/orders", orderController::getAll);
        app.get("/api/orders/{public_id}", orderController::getByPublicId);
        app.patch("/api/orders/{public_id}/status", orderController::updateStatus);
    }
}
