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
    public Customer create(Customer customer) {
        customer.setPublicId(UUID.randomUUID().toString());
        return repository.create(customer);
    }

    @Override
    public Customer findByPublicId(String publicId) {
        return repository.findByPublicId(publicId);
    }

    @Override
    public List<Customer> getAll() {
        return repository.findAll();
    }

    @Override
    public Customer update(String publicId, Customer customer) {
        return repository.update(publicId, customer);
    }
}
