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
    public void createPayment(Context context) {
        Payment payment = context.bodyAsClass(Payment.class);
        Payment saved = service.createPayment(payment);
        context.json(saved).status(201);
    }

    // Get payment by publicId
    public void getPaymentById(Context context) {
        String publicId = context.pathParam("publicId");
        Payment payment = service.findPaymentById(publicId);

        if (payment == null) {
            context.status(404).result("Payment not found");
            return;
        }

        context.json(payment);
    }

    // Update payment status
    public void updatePaymentStatus(Context context) {
        String publicId = context.pathParam("publicId");
        Payment payment = context.bodyAsClass(Payment.class); // expects JSON { "status": "PAID" }

        Payment updated = service.updatePaymentStatus(publicId, payment.getStatus().name());

        if (updated == null) {
            context.status(404).result("Payment not found");
            return;
        }

        context.json(updated);
    }
}
