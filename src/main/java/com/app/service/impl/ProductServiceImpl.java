package com.app.service.impl;

import com.app.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductServiceImpl {
    Product create(Product product);
    List<Product> getAll();
    Product getById(long id);
    Product update(long id, Product product);
    boolean delete(long id);
}
