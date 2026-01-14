package com.app.repository.impl;

import com.app.model.Payment;

public interface PaymentRepositoryImpl {

    Payment insertPayment(Payment payment);                     // must return Payment
    Payment findPaymentById(String publicId);
    Payment updatePaymentStatus(String publicId, String status);

}
