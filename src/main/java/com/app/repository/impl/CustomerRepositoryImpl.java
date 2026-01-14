package com.app.repository.impl;

import com.app.model.Customer;
import java.util.List;

public interface CustomerRepositoryImpl {

    Customer insertCustomer(Customer customer);
    Customer findCustomerById(String publicId);
    List<Customer> findAllCustomers();
    Customer updateCustomer(String publicId, Customer customer);

}