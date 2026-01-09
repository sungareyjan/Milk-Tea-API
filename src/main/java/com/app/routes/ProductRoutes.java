package com.app.routes;

import com.app.controller.ProductController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class ProductRoutes {
    private final ProductController productController;

    public  ProductRoutes(ProductController productController){
        this.productController = productController;
    }

    public  void routes(Javalin app){
        app.before("/api/products/*",context -> RoleMiddleware.allow(context, Role.ADMIN));

        app.get("api/products",productController::getAll);
        app.get("/api/products/{id}", productController::getById);
        app.post("/api/products", productController::create);
        app.put("/api/products/{id}", productController::update);
    }
}
