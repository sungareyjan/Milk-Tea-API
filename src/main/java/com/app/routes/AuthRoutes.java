package com.app.routes;

import com.app.controller.AuthController;
import io.javalin.Javalin;

public class AuthRoutes {

    private final AuthController authController;

    public AuthRoutes(AuthController authController) {
        this.authController = authController;
    }

    public void routes(Javalin app) {
        app.post("/login", authController::login);
        app.post("/logout", authController::logout);
    }
}
