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
    public void getDefault(Context context) {
        Merchant merchant = service.getDefault();
        context.json(merchant);
    }

    // Create new merchant
    public void create(Context context) {
        Merchant merchant = context.bodyAsClass(Merchant.class);
        Merchant saved = service.save(merchant);
        context.json(saved);
    }

    // Update merchant
    public void update(Context context) {
        Merchant merchant = context.bodyAsClass(Merchant.class);
        boolean updated = service.update(merchant);
        if (updated) {
            context.status(200).result("Merchant updated successfully");
        } else {
            context.status(404).result("Merchant not found");
        }
    }

    // Find by publicId
    public void findById(Context context) {
        String publicId = context.pathParam("public_id");
        Merchant merchant = service.findByPublicId(publicId);
        if (merchant != null) {
            context.json(merchant);
        } else {
            context.status(404).result("Merchant not found");
        }
    }
}
