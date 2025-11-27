package com.app.repository.productcategory;

import com.app.model.ProductCategory;

import java.sql.SQLException;
import java.util.List;

public interface ProductCategoryRepository {
    List<ProductCategory> findAll() throws SQLException;
    ProductCategory findById(int id) throws SQLException;
    void save(ProductCategory category) throws SQLException;
    void update(ProductCategory category) throws SQLException;
    void delete(int id) throws SQLException;
}
