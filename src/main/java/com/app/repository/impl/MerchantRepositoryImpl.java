package com.app.repository.impl;

import com.app.model.Merchant;

public interface MerchantRepositoryImpl {

    Merchant findDefault();
    Merchant findByPublicId(String publicId);
    Merchant save(Merchant merchant);
    boolean update(Merchant merchant);

}
