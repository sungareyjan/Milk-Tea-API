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
    public Payment create(Payment payment) {
        // assign publicId if not set
        if (payment.getPublicId() == null || payment.getPublicId().isEmpty()) {
            payment.setPublicId(java.util.UUID.randomUUID().toString());
        }

        // default status
        if (payment.getStatus() == null) {
            payment.setStatus(com.app.model.enums.PaymentStatus.PENDING);
        }

        return repository.save(payment);
    }

    @Override
    public Payment findByPublicId(String publicId) {
        return repository.findByPublicId(publicId);
    }

    @Override
    public Payment updateStatus(String publicId, String status) {
        return repository.updateStatus(publicId, status);
    }
}
