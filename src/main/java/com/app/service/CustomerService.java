package com.app.service;

import com.app.model.Customer;
import com.app.repository.CustomerRepository;
import com.app.service.impl.CustomerServiceImpl;

import java.util.List;
import java.util.UUID;

public class CustomerService implements CustomerServiceImpl {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        customer.setPublicId(UUID.randomUUID().toString());
        return repository.insertCustomer(customer);
    }

    @Override
    public Customer findCustomerById(String publicId) {
        return repository.findCustomerById(publicId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return repository.findAllCustomers();
    }

    @Override
    public Customer updateCustomer(String publicId, Customer customer) {
        return repository.updateCustomer(publicId, customer);
    }
}
