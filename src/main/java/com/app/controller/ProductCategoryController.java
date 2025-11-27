package com.app.controller;

import com.app.model.ProductCategory;
import com.app.service.ProductCategoryService;
import io.javalin.http.Context;
import java.sql.SQLException;

public class ProductCategoryController {

    private final ProductCategoryService service;

    public ProductCategoryController(ProductCategoryService service) {
        this.service = service;
    }

    public void getAll(Context ctx) throws SQLException {
        ctx.json(service.getAllCategories());
    }

    public void getById(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ProductCategory category = service.getCategory(id);
        if (category != null) ctx.json(category);
        else ctx.status(404).result("Category not found");
    }

    public void create(Context ctx) throws SQLException {
        ProductCategory category = ctx.bodyAsClass(ProductCategory.class);
        service.createCategory(category);
        ctx.status(201).json(category);
    }

    public void update(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        ProductCategory category = ctx.bodyAsClass(ProductCategory.class);
        category.setId(id);
        service.updateCategory(category);
        ctx.json(category);
    }

    public void delete(Context ctx) throws SQLException {
        int id = Integer.parseInt(ctx.pathParam("id"));
        service.deleteCategory(id);
        ctx.status(204);
    }
}
