package com.app.service;

import com.app.model.Merchant;
import com.app.repository.MerchantRepository;
import com.app.service.impl.MerchantServiceImpl;

public class MerchantService implements MerchantServiceImpl {

    private final MerchantRepository repository;

    public MerchantService(MerchantRepository repository) {
        this.repository = repository;
    }

    @Override
    public Merchant getDefaultMerchant() {
        Merchant merchant = repository.findMerchantFirst();

        if (merchant == null) {
            throw new RuntimeException("No merchant found in database");
        }

        return merchant;
    }

    @Override
    public Merchant findMerchantById(String publicId) {
        return repository.findMerchantById(publicId);
    }

    @Override
    public boolean updateMerchant(Merchant merchant) {
        return repository.updateMerchant(merchant);
    }
}
