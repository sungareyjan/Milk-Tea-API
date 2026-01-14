package com.app.controller;

import com.app.model.Customer;
import com.app.service.CustomerService;
import io.javalin.http.Context;

import java.util.Map;

public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    public void createCustomer(Context context) {
        Customer customer = context.bodyAsClass(Customer.class);
        context.status(201).json(service.createCustomer(customer));
    }

    public void getAllCustomers(Context context) {
        context.json(service.getAllCustomers());
    }

    public void getCustomerById(Context context) {
        String publicId = context.pathParam("public_id");
        Customer customer = service.findCustomerById(publicId);
        if (customer == null) {
            context.status(404).json(Map.of(
                "message", "Customer not found"
            ));
            return;
        }
        context.json(customer);
    }

    public void updateCustomer(Context context) {

        String publicId = context.pathParam("public_id");
        Customer customer = context.bodyAsClass(Customer.class);
        Customer updated = service.updateCustomer(publicId, customer);
        if (updated == null) {
            context.status(404).json(Map.of(
               "message", "Customer not found"
            ));
            return;
        }

        context.json(updated);
    }

}
