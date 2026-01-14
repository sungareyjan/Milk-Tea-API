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
        // Middleware for role checking
        app.before("/api/products/*",context -> RoleMiddleware.allow(context, Role.ADMIN));

        // Routes
        app.get("api/products",productController::getAllProducts);
        app.get("/api/products/{id}", productController::getProductById);
        app.post("/api/products", productController::createProduct);
        app.put("/api/products/{id}", productController::updateProduct);
    }
}
