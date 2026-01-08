package com.app.repository.impl;

import com.app.model.ProductCategory;

import java.sql.SQLException;
import java.util.List;

public interface ProductCategoryRepositoryImpl {
    List<ProductCategory> findAll() throws SQLException;
    ProductCategory findById(int id) throws SQLException;
    ProductCategory save(ProductCategory category) throws SQLException;
    void update(ProductCategory category) throws SQLException;
    boolean softDelete(int id) throws SQLException;
}
