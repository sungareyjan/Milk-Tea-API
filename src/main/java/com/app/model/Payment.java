package com.app.model;

import com.app.model.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Payment {
    private Long id;
    private String publicId;
    private String publicOrderId;

    private String paymentMethodName;
    private String paymentMethodDescription;

    private BigDecimal amountPaid;
    private PaymentStatus status;
}
