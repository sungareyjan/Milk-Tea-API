package com.app.service;

import com.app.model.Merchant;
import com.app.model.Order;
import com.app.model.Payment;
import com.app.model.enums.OrderStatus;
import com.app.repository.OrderRepository;
import com.app.service.impl.OrderServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService implements OrderServiceImpl {

    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order create(Order order) {
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }
        return repository.create(order);
    }

    @Override
    public Order findByPublicId(String publicId) {
        return repository.findByPublicId(publicId);
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll();
    }

    @Override
    public Order updateStatus(String publicId, OrderStatus status) {
        return repository.updateStatus(publicId, status.name());
    }

    public Map<String, Object> buildReceipt(Order order, Merchant merchant) {
        Map<String, Object> receipt = new HashMap<>();

        receipt.put("order", Map.of(
                "publicId", order.getPublicId(),
                "status", order.getStatus().name(),
                "createdAt", order.getCreatedAt(),
                "customer", Map.of(
                        "publicCustomerId", order.getPublicCustomerId()
                )
        ));

        receipt.put("items", order.getItems().stream()
                .map(item -> Map.of(
                        "name", item.getProductName(),
                        "quantity", item.getQuantity(),
                        "unitPrice", item.getUnitPrice(),
                        "subtotal", item.getSubtotal()
                ))
                .toList()
        );

        receipt.put("pricing", Map.of(
                "itemsSubtotal", order.getItemsSubtotal(),
                "deliveryFee", order.getDeliveryFee(),
                "serviceFee", order.getServiceFee(),
                "discount", order.getDiscount(),
                "totalAmount", order.getTotalAmount()
        ));

        Payment payment = order.getPayment();
        receipt.put("payment", Map.of(
                "method", payment != null ? payment.getMethod().name() : "CASH",
                "status", payment != null ? payment.getStatus().name() : "PENDING"
        ));

        receipt.put("merchant", Map.of(
                "name", merchant.getName(),
                "branch", merchant.getBranch()
        ));

        return receipt;
    }


}
