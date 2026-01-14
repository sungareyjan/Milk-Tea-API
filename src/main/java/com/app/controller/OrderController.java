package com.app.controller;

import com.app.model.Merchant;
import com.app.model.Order;
import com.app.model.enums.OrderStatus;
import com.app.service.MerchantService;
import com.app.service.OrderService;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class OrderController {

    private final OrderService orderService;
    private final MerchantService merchantService;

    public OrderController(OrderService orderService, MerchantService merchantService) {
        this.orderService = orderService;
        this.merchantService = merchantService;
    }

    // Create Order
    public void createOrder(Context context) {
        Order order = context.bodyAsClass(Order.class);

        Order saved = orderService.createOrder(order);
        Merchant merchant = merchantService.getDefault();

        context.json(orderService.buildReceipt(saved, merchant));
    }

    // Get single order by public ID
    public void getOrderById(Context context) {
        String publicId = context.pathParam("public_id");
        Order order = orderService.findOrderById(publicId);
        if (order == null) {
            context.status(404).result("Order not found");
        } else {
            context.json(order);
        }
    }

    // Get all orders
    public void getAllOrders(Context context) {
        List<Order> orders = orderService.findAllOrders();
        context.json(orders);
    }

    public void updateOrderStatus(Context context) {
        String publicId = context.pathParam("public_id");

        // Get status from request body as a simple JSON string, e.g., { "status": "PAID" }
        Map<String, String> body = context.bodyAsClass(Map.class);
        String statusStr = body.get("status");

        if (statusStr == null) {
            context.status(400).result("Missing 'status' in request body");
            return;
        }

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            context.status(400).result("Invalid order status: " + statusStr);
            return;
        }

        Order updated = orderService.updateOrderStatus(publicId, status);
        if (updated == null) {
            context.status(404).result("Order not found");
        } else {
            context.json(updated);
        }
    }

}
