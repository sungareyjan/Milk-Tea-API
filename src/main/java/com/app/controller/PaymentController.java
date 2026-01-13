package com.app.controller;

import com.app.model.Payment;
import com.app.service.PaymentService;
import io.javalin.http.Context;

public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    // Create a new payment
    public void create(Context ctx) {
        Payment payment = ctx.bodyAsClass(Payment.class);
        Payment saved = service.create(payment);
        ctx.json(saved).status(201);
    }

    // Get payment by publicId
    public void getByPublicId(Context ctx) {
        String publicId = ctx.pathParam("publicId");
        Payment payment = service.findByPublicId(publicId);

        if (payment == null) {
            ctx.status(404).result("Payment not found");
            return;
        }

        ctx.json(payment);
    }

    // Update payment status
    public void updateStatus(Context ctx) {
        String publicId = ctx.pathParam("publicId");
        Payment payment = ctx.bodyAsClass(Payment.class); // expects JSON { "status": "PAID" }

        Payment updated = service.updateStatus(publicId, payment.getStatus().name());

        if (updated == null) {
            ctx.status(404).result("Payment not found");
            return;
        }

        ctx.json(updated);
    }
}
