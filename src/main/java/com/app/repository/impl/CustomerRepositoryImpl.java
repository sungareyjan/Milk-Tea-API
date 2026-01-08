package com.app.repository.impl;


import com.app.model.Customer;
import java.util.List;

public interface CustomerRepositoryImpl {
    Customer create(Customer customer);
    Customer findByPublicId(String publicId);
    List<Customer> findAll();
    Customer update(String publicId, Customer customer);
}