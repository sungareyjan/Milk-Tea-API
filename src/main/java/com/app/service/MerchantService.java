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
    public Merchant findByPublicId(String publicId) {
        return repository.findByPublicId(publicId);
    }

    @Override
    public Merchant save(Merchant merchant) {
        return repository.save(merchant);
    }

    @Override
    public boolean update(Merchant merchant) {
        return repository.update(merchant);
    }

    public Merchant getDefault() {
        Merchant merchant = repository.findDefault();

        if (merchant == null) {
            return Merchant.builder()
                .name("RJ Codes Elit Milk Tea")
                .branch("San Fernando, Pampanga")
                .build();
        }

        return merchant;
    }
}
