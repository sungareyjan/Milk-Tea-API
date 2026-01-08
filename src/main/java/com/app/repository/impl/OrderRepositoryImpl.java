package com.app.repository.impl;

import com.app.model.Order;

import java.util.List;

public interface OrderRepositoryImpl {

    Order create(Order order);
    Order findByPublicId(String publicId);
    List<Order> findAll();
    Order updateStatus(String publicId, String status);
}
