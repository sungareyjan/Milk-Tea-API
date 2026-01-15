package com.app.controller;

import com.app.model.Merchant;
import com.app.service.MerchantService;
import io.javalin.http.Context;

public class MerchantController {

    private final MerchantService service;

    public MerchantController(MerchantService service) {
        this.service = service;
    }

    public void getDefault(Context context) {
        context.json(service.getDefaultMerchant());
    }

    public void findByPublicId(Context context) {
        String publicId = context.pathParam("public_id");
        Merchant merchant = service.findMerchantById(publicId);
        if (merchant == null) {
            context.status(404).result("Merchant not found");
            return;
        }
        context.json(merchant);
    }

    public void update(Context context) {
        Merchant merchant = context.bodyAsClass(Merchant.class);

        boolean updated = service.updateMerchant(merchant);
        if (!updated) {
            context.status(404).result("Merchant not found");
            return;
        }

        context.result("Merchant updated successfully");
    }
}
