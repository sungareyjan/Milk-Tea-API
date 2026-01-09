package com.app.service;

import com.app.model.Order;
import com.app.model.enums.OrderStatus;
import com.app.repository.OrderRepository;
import com.app.service.impl.OrderServiceImpl;

import java.util.List;

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

}
