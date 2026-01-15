package com.app.service.impl;

import com.app.model.Merchant;

public interface MerchantServiceImpl {

    Merchant getDefaultMerchant();
    Merchant findMerchantById(String publicId);
    boolean updateMerchant(Merchant merchant);
}
