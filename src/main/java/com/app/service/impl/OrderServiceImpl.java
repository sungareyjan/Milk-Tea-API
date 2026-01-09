package com.app.service.impl;

import com.app.model.Order;
import com.app.model.enums.OrderStatus;

import java.util.List;
public interface OrderServiceImpl {

    Order create(Order order);
    Order findByPublicId(String publicId);
    List<Order> findAll();
    Order updateStatus(String publicId, OrderStatus status);

}
