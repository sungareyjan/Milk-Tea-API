package com.app.middleware;

import com.app.util.TokenUtils;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public class AuthMiddleware {

    public static void protectRoute(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedResponse("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        if (!TokenUtils.verifyToken(token)) {
            throw new UnauthorizedResponse("Invalid or expired token");
        }
    }
}
