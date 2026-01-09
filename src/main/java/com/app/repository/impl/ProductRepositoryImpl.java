package com.app.repository.impl;

import com.app.model.Product;
import com.app.model.User;

import java.sql.SQLException;
import java.util.List;

public interface ProductRepositoryImpl {

//    List<Product> findAll() throws SQLException;
//    Product save(Product product) throws SQLException;
//    Product findById(int id) throws SQLException;
//    void update(Product product) throws SQLException;

    Product create(Product product);
    Product findById(long id);
    List<Product> findAll();
    Product update(long id, Product product);
    boolean delete(long id);
}
