package com.app.routes;

import com.app.controller.PaymentController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class PaymentRoutes {

    private final PaymentController paymentController;

    public PaymentRoutes(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    public void routes(Javalin app) {
        // Middleware for role checking
        app.before("/api/payments/*", context -> RoleMiddleware.allow(context, Role.ADMIN));

        // Routes
        app.post("/api/payments", paymentController::createPayment);
        app.get("/api/payments/{publicId}", paymentController::getPaymentById);
        app.patch("/api/payments/{publicId}/status", paymentController::updatePaymentStatus);
    }
}
