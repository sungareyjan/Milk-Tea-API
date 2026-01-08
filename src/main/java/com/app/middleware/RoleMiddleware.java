package com.app.middleware;

import com.app.model.User;
import com.app.model.enums.Role;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.Arrays;

public class RoleMiddleware {

    public static void allow(Context context, Role... allowedRoles) {
        User user = context.attribute("user");

        if (user == null) {

            throw new UnauthorizedResponse("Unauthorized");
        }

        boolean allowed = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == user.getRole());

        if (!allowed) {
            throw new ForbiddenResponse("Forbidden: insufficient role");
        }
    }
}
