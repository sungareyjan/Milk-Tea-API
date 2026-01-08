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
    public void login(Context context) {
        try {
            String username = context.formParam("username");
            String password = context.formParam("password");

            String token = authService.login(username, password);
            context.status(200).json("{\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            context.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    public void logout(Context context) {
        // Client can just remove token
        context.status(200).json("{\"message\":\"Logged out\"}");
    }

}
