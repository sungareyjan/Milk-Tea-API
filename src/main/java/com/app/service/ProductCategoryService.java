package com.app.service;

import com.app.model.ProductCategory;
import com.app.repository.productcategory.ProductCategoryRepositoryImplementation;
import java.sql.SQLException;
import java.util.List;

public class ProductCategoryService {

    private final ProductCategoryRepositoryImplementation repository;

    public ProductCategoryService(ProductCategoryRepositoryImplementation repository) {
        this.repository = repository;
    }

    public List<ProductCategory> getAllCategories() throws SQLException {
        return repository.findAll();
    }

    public ProductCategory getCategory(int id) throws SQLException {
        return repository.findById(id);
    }

    public void createCategory(ProductCategory category) throws SQLException {
        repository.save(category);
    }

    public void updateCategory(ProductCategory category) throws SQLException {
        repository.update(category);
    }

    public void deleteCategory(int id) throws SQLException {
        repository.delete(id);
    }
}
