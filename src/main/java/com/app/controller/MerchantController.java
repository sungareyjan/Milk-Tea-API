package com.app.controller;

import com.app.model.Merchant;
import com.app.service.MerchantService;
import io.javalin.http.Context;

public class MerchantController {

    private final MerchantService service;

    public MerchantController(MerchantService service) {
        this.service = service;
    }

    // Get default merchant
    public void getDefault(Context ctx) {
        Merchant merchant = service.getDefault();
        ctx.json(merchant);
    }

    // Create new merchant
    public void create(Context ctx) {
        Merchant merchant = ctx.bodyAsClass(Merchant.class);
        Merchant saved = service.save(merchant);
        ctx.json(saved);
    }

    // Update merchant
    public void update(Context ctx) {
        Merchant merchant = ctx.bodyAsClass(Merchant.class);
        boolean updated = service.update(merchant);
        if (updated) {
            ctx.status(200).result("Merchant updated successfully");
        } else {
            ctx.status(404).result("Merchant not found");
        }
    }

    // Find by publicId
    public void findById(Context ctx) {
        String publicId = ctx.pathParam("public_id");
        Merchant merchant = service.findByPublicId(publicId);
        if (merchant != null) {
            ctx.json(merchant);
        } else {
            ctx.status(404).result("Merchant not found");
        }
    }
}
