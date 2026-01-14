package com.app.service.impl;

import com.app.model.Merchant;

public interface MerchantServiceImpl {

    Merchant findByPublicId(String publicId);
    Merchant save(Merchant merchant);
    boolean update(Merchant merchant);

    // Default: return first merchant if none specified
    default Merchant getDefault() {
        Merchant merchant = findByPublicId(null);
        if (merchant == null) {
            throw new RuntimeException("No merchant found in DB");
        }
        return merchant;
    }
}
