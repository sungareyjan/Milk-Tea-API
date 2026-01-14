package com.app.service;

import com.app.model.Payment;
import com.app.repository.PaymentRepository;
import com.app.service.impl.PaymentServiceImpl;

public class PaymentService implements PaymentServiceImpl {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment createPayment(Payment payment) {
        // assign publicId if not set
        if (payment.getPublicId() == null || payment.getPublicId().isEmpty()) {
            payment.setPublicId(java.util.UUID.randomUUID().toString());
        }

        // default status
        if (payment.getStatus() == null) {
            payment.setStatus(com.app.model.enums.PaymentStatus.PENDING);
        }

        return repository.insertPayment(payment);
    }

    @Override
    public Payment findPaymentById(String publicId) {
        return repository.findPaymentById(publicId);
    }

    @Override
    public Payment updatePaymentStatus(String publicId, String status) {
        return repository.updatePaymentStatus(publicId, status);
    }
}
