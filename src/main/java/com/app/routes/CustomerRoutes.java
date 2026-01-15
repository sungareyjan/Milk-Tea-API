package com.app.routes;

import com.app.controller.CustomerController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class CustomerRoutes {
    private final CustomerController customerController;

    public  CustomerRoutes(CustomerController customerController){
        this.customerController = customerController;
    }

    public  void routes(Javalin app){
        app.before("/api/customer/*",context -> RoleMiddleware.allow(context, Role.ADMIN, Role.STAFF));

        // Routes
        app.get("api/customer",customerController::getAllCustomers);
        app.get("api/customer/{public_id}",customerController::getCustomerById);
        app.post("/api/customer", customerController::createCustomer);
        app.put("/api/customer/{public_id}", customerController::updateCustomer);

    }
}
