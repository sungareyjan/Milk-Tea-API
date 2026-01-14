package com.app.repository.impl;

import com.app.model.Product;

import java.util.List;

public interface ProductRepositoryImpl {

    Product insertProduct(Product product);
    Product findProductById(long id);
    List<Product> findAllProducts();
    Product updateProduct(long id, Product product);
    boolean delete(long id);

}
