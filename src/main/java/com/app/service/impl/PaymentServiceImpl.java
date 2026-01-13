package com.app.service.impl;

import com.app.model.Payment;

public interface PaymentServiceImpl {
    Payment create(Payment payment);
    Payment findByPublicId(String publicId);
    Payment updateStatus(String publicId, String status);
}
