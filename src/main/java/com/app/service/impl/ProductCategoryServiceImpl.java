package com.app.service.impl;

import com.app.model.ProductCategory;

import java.sql.SQLException;
import java.util.List;

public interface ProductCategoryServiceImpl {

    List<ProductCategory> getAllCategories() throws SQLException;
    ProductCategory getCategory(int id) throws SQLException;
    void createCategory(ProductCategory category) throws SQLException;
    void updateCategory(ProductCategory category) throws SQLException;
    boolean  softDeleteCategory(int id) throws SQLException;

}
