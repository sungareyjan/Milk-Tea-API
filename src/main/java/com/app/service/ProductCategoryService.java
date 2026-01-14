package com.app.service;

import com.app.model.ProductCategory;
import com.app.repository.ProductCategoryRepository;
import com.app.service.impl.ProductCategoryServiceImpl;

import java.sql.SQLException;
import java.util.List;

public class ProductCategoryService implements ProductCategoryServiceImpl {

    private final ProductCategoryRepository repository;

    public ProductCategoryService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void createCategory(ProductCategory category) throws SQLException {
        repository.insertProductCategory(category);
    }

    @Override
    public List<ProductCategory> getAllCategories() throws SQLException {
        return repository.findAllProductCategories();
    }

    @Override
    public ProductCategory getCategory(int id) throws SQLException {
        return repository.findProductCategoryById(id);
    }

    @Override
    public void updateCategory(ProductCategory category) throws SQLException {
        repository.updateProductCategory(category);
    }

    @Override
    public boolean softDeleteCategory(int id) throws SQLException {
      return repository.softDeleteProductCategory(id);
    }

}
