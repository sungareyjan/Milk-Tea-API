package com.app.controller;

import com.app.service.AuthService;
import io.javalin.http.Context;
import lombok.Getter;

@Getter
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --- Javalin route handler ---
    public void login(Context ctx) {
        try {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            String token = authService.login(username, password);
            ctx.status(200).json("{\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            ctx.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    public void logout(Context ctx) {
        // Client can just remove token
        ctx.status(200).json("{\"message\":\"Logged out\"}");
    }

}
