package com.app;

import com.app.config.Env;
import com.app.controller.*;
import com.app.database.DBConnection;
import com.app.exception.GlobalExceptionHandler;
import com.app.middleware.AuthMiddleware;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import com.app.repository.*;
import com.app.routes.*;
import com.app.service.*;
import com.app.migration.MigrationRunner;
import com.app.seeder.SeederRunner;
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

            GlobalExceptionHandler.register(app);

            // --- AUTH ROUTES ---
            app.before("/api/*", AuthMiddleware::protectRoute);
            // Sample route for api route check
            app.get("/api/hello", context -> {
                context.result("Hello World!");
            });

            // --- Register Routes ---
            AuthController authController = new AuthController(new AuthService(new AuthRepository(connection)));
            new AuthRoutes(authController).routes(app);

            UserController userController = new UserController(new UserService(new UserRepository(connection)));
            new UserRoutes(userController).routes(app);

            ProductCategoryController categoryController = new ProductCategoryController(new ProductCategoryService(new ProductCategoryRepository(connection)));
            new ProductCategoryRoutes(categoryController).routes(app);

            ProductController productController = new ProductController(new ProductService(new ProductRepository(connection)));
            new ProductRoutes(productController).routes(app);

            CustomerController customerController = new CustomerController(new CustomerService(new CustomerRepository(connection)));
            new CustomerRoutes(customerController).routes(app);

            // --- MERCHANT ---
            MerchantRepository merchantRepository = new MerchantRepository(connection);
            MerchantService merchantService = new MerchantService(merchantRepository);

            // --- ORDER ---
            OrderRepository orderRepository = new OrderRepository(connection);
            OrderService orderService = new OrderService(orderRepository);

            OrderController orderController = new OrderController(orderService, merchantService);

            new OrderRoutes(orderController).routes(app);

            app.start(Integer.parseInt(Env.get("PORT", "8000")));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create server", e);
        }
    }
}
