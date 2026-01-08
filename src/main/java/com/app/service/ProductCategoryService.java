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
        repository.save(category);
    }

    @Override
    public List<ProductCategory> getAllCategories() throws SQLException {
        return repository.findAll();
    }

    @Override
    public ProductCategory getCategory(int id) throws SQLException {
        return repository.findById(id);
    }

    @Override
    public void updateCategory(ProductCategory category) throws SQLException {
        repository.update(category);
    }

    @Override
    public boolean softDeleteCategory(int id) throws SQLException {
      return repository.softDelete(id);
    }

}
