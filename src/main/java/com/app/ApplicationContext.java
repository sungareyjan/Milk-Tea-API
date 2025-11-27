package com.app;

import com.app.controller.AuthController;
import com.app.controller.ProductCategoryController;
import com.app.database.DBConnection;
import com.app.middleware.AuthMiddleware;
import com.app.repository.AuthRepositoryImplementation;
import com.app.repository.productcategory.ProductCategoryRepositoryImplementation;
import com.app.service.AuthService;
import com.app.migration.MigrationRunner;
import com.app.seeder.SeederRunner;
import com.app.service.ProductCategoryService;
import io.javalin.Javalin;

import java.sql.Connection;

public class ApplicationContext {

    public static void start() {
        // Run migrations & seeders
        MigrationRunner.run();
        SeederRunner.runAll();
        try {
            // Initialize database connection
            Connection connection = DBConnection.getConnection();

            // Create Javalin app
            Javalin app = Javalin.create(config -> {
                config.http.defaultContentType = "application/json";
            });

            // --- AUTH ROUTES ---
            app.before("/api/*", AuthMiddleware::protectRoute);

            AuthRepositoryImplementation authRepository = new AuthRepositoryImplementation(connection);
            AuthService authService = new AuthService(authRepository);
            AuthController authController = new AuthController(authService);

            // Public routes
            app.post("/login", authController::login);
            app.post("/logout", authController::logout);

            // Sample route
            app.get("/api/hello", ctx -> {
                ctx.result("Hello World!");
            });

            ProductCategoryRepositoryImplementation categoryRepo = new ProductCategoryRepositoryImplementation(connection);
            ProductCategoryService categoryService = new ProductCategoryService(categoryRepo);
            ProductCategoryController categoryController = new ProductCategoryController(categoryService);

            // Product category routes
            app.get("/api/product-categories", categoryController::getAll);
            app.get("/api/product-categories/{id}", categoryController::getById);
            app.post("/api/product-categories", categoryController::create);
            app.put("/api/product-categories/{id}", categoryController::update);
            app.delete("/api/product-categories/{id}", categoryController::delete);

            // Start server
            app.start(8000);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create server", e);
        }
    }
}
