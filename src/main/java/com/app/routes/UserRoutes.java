package com.app.routes;

import com.app.controller.UserController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class UserRoutes {

    private final UserController userController;

    public UserRoutes(UserController userController) {
        this.userController = userController;
    }

    public void routes(Javalin app) {
        // Admin-only middleware
        app.before("/api/users/*", context -> RoleMiddleware.allow(context, Role.ADMIN));

        // User CRUD routes
        app.get("/api/users", userController::getAllUsers);
        app.get("/api/users/{public_id}", userController::getUserById);
        app.post("/api/users", userController::createUser);
        app.put("/api/users/{public_id}", userController::updateUser);
    }
}
