package com.app.model;

import com.app.model.enums.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
    private OrderStatus status; // use your enum to ensure only valid statuses
}
