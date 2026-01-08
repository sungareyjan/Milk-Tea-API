package com.app.routes;

import com.app.controller.ProductCategoryController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class ProductCategoryRoutes {

    private final ProductCategoryController productCategoryController;

    public ProductCategoryRoutes(ProductCategoryController controller) {
        this.productCategoryController = controller;
    }

    public void routes(Javalin app) {
        // Middleware for role checking
        app.before("/api/product-categories/*", context -> RoleMiddleware.allow(context, Role.ADMIN));

        // Routes
        app.get("/api/product-categories", productCategoryController::getAll);
        app.get("/api/product-categories/{id}", productCategoryController::getById);
        app.post("/api/product-categories", productCategoryController::create);
        app.put("/api/product-categories/{id}", productCategoryController::update);
        app.patch("/api/product-categories/{id}", productCategoryController::delete);
    }
}
