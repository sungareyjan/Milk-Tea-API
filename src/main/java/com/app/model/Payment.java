package com.app.model;

import com.app.model.enums.PaymentMethod;
import com.app.model.enums.PaymentStatus;
import lombok.Data;

@Data
public class Payment {
    private PaymentMethod method;
    private PaymentStatus status;
}
