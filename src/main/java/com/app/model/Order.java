package com.app.model;

import com.app.model.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @JsonIgnore
    private long id;

    private String publicId;
    private String publicCustomerId;
    private OrderStatus status; // enum
    private String createdBy;
    private LocalDateTime createdAt;

    // pricing
    private BigDecimal itemsSubtotal;
    private BigDecimal deliveryFee;
    private BigDecimal serviceFee;
    private BigDecimal discount;
    private BigDecimal totalAmount;

//    private LocalDateTime updatedAt;
    private List<OrderItem> items;
    private Payment payment;
    private Merchant merchant;
}
