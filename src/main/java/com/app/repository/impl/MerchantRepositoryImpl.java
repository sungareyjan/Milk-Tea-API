package com.app.repository.impl;

import com.app.model.Merchant;

public interface MerchantRepositoryImpl {

    Merchant findMerchantById(String publicId);
    Merchant findMerchantFirst(); // default merchant
    boolean updateMerchant(Merchant merchant);

}
