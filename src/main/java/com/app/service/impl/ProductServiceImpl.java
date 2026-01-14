package com.app.service.impl;

import com.app.model.Product;

import java.util.List;

public interface ProductServiceImpl {

    Product createProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(long id);
    Product updateProduct(long id, Product product);
    boolean delete(long id);

}
