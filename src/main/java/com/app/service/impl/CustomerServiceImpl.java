package com.app.service.impl;

import com.app.model.Customer;

import java.util.List;

public interface CustomerServiceImpl {
    Customer create(Customer customer);
    Customer findByPublicId(String publicId);
    List<Customer> getAll();
    Customer update(String publicId, Customer customer);
}
