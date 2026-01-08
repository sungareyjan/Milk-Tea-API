package com.app.repository;

import com.app.exception.DuplicateResourceException;
import com.app.model.ProductCategory;
import com.app.repository.impl.ProductCategoryRepositoryImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryRepository implements ProductCategoryRepositoryImpl {

    private final Connection connection;

    public ProductCategoryRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ProductCategory> findAll() throws SQLException {
        List<ProductCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM product_categories";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                categories.add(new ProductCategory(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description")
                ));
            }
        }
        return categories;
    }

    @Override
    public ProductCategory findById(int id) throws SQLException {
        String query = "SELECT * FROM product_categories WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new ProductCategory(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description")
                );
            }
        }
        return null;
    }

    @Override
    public ProductCategory save(ProductCategory category) throws SQLException {

        connection.setAutoCommit(false); // Start transaction
        // Insert new category
        String insertQuery = "INSERT INTO product_categories (name, description) VALUES (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();

            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                category.setId(keys.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // handle duplicates here
            if (e.getMessage().contains("product_categories.name")) {
                throw new DuplicateResourceException("Catergory'" + category.getName() + "' already exists.");
            } else {
                throw e; // rethrow other constraint violations
            }
        }
        connection.commit();
        connection.setAutoCommit(true);
        return category;
    }

    @Override
    public void update(ProductCategory productCategory) throws SQLException {
        connection.setAutoCommit(false); // Start transaction
        String query = "UPDATE product_categories SET name=?, description=? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productCategory.getName());
            preparedStatement.setString(2, productCategory.getDescription());
            preparedStatement.setInt(3, productCategory.getId());
            preparedStatement.executeUpdate();
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    @Override
    public boolean softDelete(int id) throws SQLException {
        connection.setAutoCommit(false); // Start transaction
        String query = "UPDATE product_categories SET deleted = 1 WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            return rows > 0;
        }
    }
}
