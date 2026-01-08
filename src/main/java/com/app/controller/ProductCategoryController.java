package com.app.controller;

import com.app.model.ProductCategory;
import com.app.service.ProductCategoryService;
import io.javalin.http.Context;
import java.sql.SQLException;

public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService service) {
        this.productCategoryService = service;
    }

    public void getAll(Context context) throws SQLException {
        context.json(productCategoryService.getAllCategories());
    }

    public void getById(Context context) throws SQLException {
        int id = Integer.parseInt(context.pathParam("id"));
        ProductCategory category = productCategoryService.getCategory(id);
        if (category != null) {
            context.json(category);
        }
        else{
            context.status(404).result("Category not found");
        }
    }

    public void create(Context context) throws SQLException {
        ProductCategory category = context.bodyAsClass(ProductCategory.class);
        productCategoryService.createCategory(category);
        context.status(201).json(category);
    }

    public void update(Context context) throws SQLException {
        int id = Integer.parseInt(context.pathParam("id"));
        ProductCategory category = context.bodyAsClass(ProductCategory.class);
        category.setId(id);
        productCategoryService.updateCategory(category);
        context.json(category);
    }

    public void delete(Context context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            if (productCategoryService.softDeleteCategory(id)) {
                context.status(204); // No Content
            } else {
                context.status(404); // Not Found
            }
        } catch (Exception e) {
            context.status(500); // Server Error
        }
    }
}
