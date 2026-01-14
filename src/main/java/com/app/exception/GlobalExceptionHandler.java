package com.app.exception;

import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;
import java.util.Map;

public class GlobalExceptionHandler {

    // Helper to handle common client errors
    private static void handleClientError(Javalin app, Class<? extends RuntimeException> exceptionClass, int status, String error) {
        app.exception(exceptionClass, (e, context) -> {
            context.status(status).json(Map.of(
                    "status", status,
                    "error", error,
                    "message", e.getMessage()
            ));
        });
    }

    public static void register(Javalin app) {

        // Register common 4xx HTTP exceptions
        handleClientError(app, BadRequestResponse.class, 400, "Bad Request");
        handleClientError(app, UnauthorizedResponse.class, 401, "Unauthorized");
        handleClientError(app, ForbiddenResponse.class, 403, "Forbidden");
        handleClientError(app, NotFoundResponse.class, 404, "Not Found");

        // Handle duplicate resource (like duplicate username)
        app.exception(DuplicateResourceException.class, (e, context) -> {
            context.status(400).json(Map.of(
                    "error", "Duplicate Entry",
                    "message", e.getMessage()
            ));
        });

        // Handle SQL exceptions
        app.exception(SQLException.class, (e, context) -> {
            e.printStackTrace(); // log internally
            context.status(500).json(Map.of(
                    "error", "Database Error",
                    "message", "A database error occurred. Please contact support."
            ));
        });

        // Catch-all handler for unexpected exceptions
        app.exception(Exception.class, (e, context) -> {
            e.printStackTrace(); // log internally
            context.status(500).json(Map.of(
                    "status", 500,
                    "error", "Internal Server Error",
                    "message", "Something went wrong on the server."
            ));
        });
        // Handle  Amount paid must be equal to order total amount
        app.exception(BusinessException.class, (e, ctx) -> {
            ctx.status(400).json(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", e.getMessage()
            ));
        });
    }
}
