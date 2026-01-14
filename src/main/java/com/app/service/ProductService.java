//package com.app.service;
//
//import com.app.model.Product;
//import com.app.repository.ProductRepository;
//
//import java.util.List;
//
//public class ProductService {
//
//    private final ProductRepository repository;
//
//    public ProductService(ProductRepository repository) {
//        this.repository = repository;
//    }
//
//    public Product create(Product product) {
//        return repository.create(product);
//    }
//
//    public List<Product> getAll() {
//        return repository.findAll();
//    }
//
//    public Product getById(long id) {
//        return repository.findById(id);
//    }
//
//    public Product update(long id, Product product) {
//        return repository.update(id, product);
//    }
//
//    public boolean delete(long id) {
//        return repository.delete(id);
//    }
//}

package com.app.service;

import com.app.model.Product;
import com.app.repository.ProductRepository;
import com.app.service.impl.ProductServiceImpl;

import java.util.List;

public class ProductService implements ProductServiceImpl {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Product createProduct(Product product) {
        return repository.insertProduct(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return repository.findAllProducts();
    }

    @Override
    public Product getProductById(long id) {
        return repository.findProductById(id);
    }

    @Override
    public Product updateProduct(long id, Product product) {
        return repository.updateProduct(id, product);
    }

    @Override
    public boolean delete(long id) {
        return repository.delete(id);
    }
}
