package com.app.repository.impl;

import com.app.model.ProductCategory;

import java.sql.SQLException;
import java.util.List;

public interface ProductCategoryRepositoryImpl {

    List<ProductCategory> findAllProductCategories() throws SQLException;
    ProductCategory findProductCategoryById(int id) throws SQLException;
    ProductCategory insertProductCategory(ProductCategory category) throws SQLException;
    void updateProductCategory(ProductCategory category) throws SQLException;
    boolean softDeleteProductCategory(int id) throws SQLException;

}
