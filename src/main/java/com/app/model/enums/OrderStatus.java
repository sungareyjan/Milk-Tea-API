package com.app.model.enums;

public enum OrderStatus {

    PENDING,
    PAID,
    CANCELLED,
    COMPLETED,
    REFUNDED;

    /**
     * Convert string to OrderStatus enum (case-insensitive).
     */
    public static OrderStatus fromString(String status) {
        if (status == null) return null;
        return OrderStatus.valueOf(status.toUpperCase());
    }
}
