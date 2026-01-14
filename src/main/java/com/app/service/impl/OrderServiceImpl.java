package com.app.service.impl;

import com.app.model.Order;
import com.app.model.enums.OrderStatus;

import java.util.List;

public interface OrderServiceImpl {

    Order createOrder(Order order);
    Order findOrderById(String publicId);
    List<Order> findAllOrders();
    Order updateOrderStatus(String publicId, OrderStatus status);

}
