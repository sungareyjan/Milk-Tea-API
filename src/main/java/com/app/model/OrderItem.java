package com.app.model;

import com.app.model.enums.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    @JsonIgnore
    private long id;
    @JsonIgnore
    private long orderId;

    private long productId;

    // SNAPSHOT DATA
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productCategoryDescription;

    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private Size size;
    private String unit;
    private BigDecimal measurement;

//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
}
