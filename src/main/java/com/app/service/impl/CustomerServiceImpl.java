package com.app.service.impl;

import com.app.model.Customer;

import java.util.List;

public interface CustomerServiceImpl {

    Customer createCustomer(Customer customer);
    Customer findCustomerById(String publicId);
    List<Customer> getAllCustomers();
    Customer updateCustomer(String publicId, Customer customer);

}
