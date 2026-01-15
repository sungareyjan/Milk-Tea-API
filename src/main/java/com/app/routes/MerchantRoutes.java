package com.app.routes;

import com.app.controller.MerchantController;
import com.app.middleware.RoleMiddleware;
import com.app.model.enums.Role;
import io.javalin.Javalin;

public class MerchantRoutes {
    private final MerchantController merchantController;

    public MerchantRoutes(MerchantController merchantController) {
        this.merchantController = merchantController;
    }

    public  void routes(Javalin app){

        // Middleware for role checking
        app.before("/api/merchant/*",context -> RoleMiddleware.allow(context, Role.ADMIN));

        // Routes
        app.get("/api/merchant/default", merchantController::getDefault);
        app.get("/api/merchant/{public_id}", merchantController::findByPublicId);
        app.put("/api/merchant", merchantController::update);
    }
}
