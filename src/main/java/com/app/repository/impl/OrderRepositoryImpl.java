package com.app.repository.impl;

import com.app.model.Order;
import java.util.List;

public interface OrderRepositoryImpl {

    Order insertOrder(Order order);
    Order findOrderById(String publicId);
    List<Order> findAllOrders();
    Order updateOrderStatus(String publicId, String status);

}
