package com.app.service.impl;

import com.app.model.Payment;

public interface PaymentServiceImpl {

    Payment createPayment(Payment payment);
    Payment findPaymentById(String publicId);
    Payment updatePaymentStatus(String publicId, String status);

}
