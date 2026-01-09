package com.app.routes;

import com.app.controller.CustomerController;
import com.app.controller.ProductController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class CustomerRoutes {
    private final CustomerController customerController;

    public  CustomerRoutes(CustomerController customerController){
        this.customerController = customerController;
    }

    public  void routes(Javalin app){
        app.before("/api/customer/*",context -> RoleMiddleware.allow(context, Role.ADMIN));

        app.get("api/customer",customerController::getAll);
        app.get("api/customer/{public_id}",customerController::getCustomer);
        app.post("/api/customer", customerController::create);
        app.put("/api/customer/{public_id}", customerController::update);

    }
}
