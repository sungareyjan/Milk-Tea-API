package com.app.repository.impl;

import com.app.model.Payment;

public interface PaymentRepositoryImpl {
    Payment save(Payment payment);                     // must return Payment
    Payment findByPublicId(String publicId);
    Payment updateStatus(String publicId, String status);
}
