package com.app.controller;

import com.app.model.Customer;
import com.app.model.User;
import com.app.service.CustomerService;
import io.javalin.http.Context;

import java.util.Map;

public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    public void create(Context ctx) {
        Customer customer = ctx.bodyAsClass(Customer.class);
        ctx.status(201).json(service.create(customer));
    }

    public void getAll(Context ctx) {
        ctx.json(service.getAll());
    }

    public void getCustomer(Context context) {
        String publicId = context.pathParam("public_id");
        Customer customer = service.findByPublicId(publicId);
        if (customer == null) {
            context.status(404).json(Map.of(
                "message", "Customer not found"
            ));
            return;
        }
        context.json(customer);
    }


    public void update(Context ctx) {

        String publicId = ctx.pathParam("public_id"); // String, NOT long
        Customer customer = ctx.bodyAsClass(Customer.class);
        Customer updated = service.update(publicId, customer);
        if (updated == null) {
            ctx.status(404).json(Map.of(
               "message", "Customer not found"
            ));
            return;
        }

        ctx.json(updated);
    }

}
