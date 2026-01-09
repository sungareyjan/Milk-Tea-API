package com.app.controller;

import com.app.model.Order;
import com.app.model.StatusRequest;
import com.app.model.enums.OrderStatus;
import com.app.service.OrderService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // Create new order
    public void create(Context ctx) {
        Order order = ctx.bodyAsClass(Order.class);
        Order created = service.create(order);
        ctx.status(201).json(created);
    }

    // Get single order by public ID
    public void getByPublicId(Context ctx) {
        String publicId = ctx.pathParam("public_id");
        Order order = service.findByPublicId(publicId);
        if (order == null) {
            ctx.status(404).result("Order not found");
        } else {
            ctx.json(order);
        }
    }

    // Get all orders
    public void getAll(Context ctx) {
        List<Order> orders = service.findAll();
        ctx.json(orders);
    }

    public void updateStatus(Context ctx) {
        String publicId = ctx.pathParam("public_id");

        // Get status from request body as a simple JSON string, e.g., { "status": "PAID" }
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String statusStr = body.get("status");

        if (statusStr == null) {
            ctx.status(400).result("Missing 'status' in request body");
            return;
        }

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid order status: " + statusStr);
            return;
        }

        Order updated = service.updateStatus(publicId, status);
        if (updated == null) {
            ctx.status(404).result("Order not found");
        } else {
            ctx.json(updated); // directly return the model
        }
    }

}
