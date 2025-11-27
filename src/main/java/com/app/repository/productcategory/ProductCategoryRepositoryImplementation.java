package com.app.repository.productcategory;

import com.app.model.ProductCategory;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryRepositoryImplementation implements ProductCategoryRepository {

    private final Connection connection;

    public ProductCategoryRepositoryImplementation(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ProductCategory> findAll() throws SQLException {
        List<ProductCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM product_categories";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {
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
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
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
    public void save(ProductCategory category) throws SQLException {
        String query = "INSERT INTO product_categories (name, description) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) category.setId(keys.getInt(1));
        }
    }

    @Override
    public void update(ProductCategory category) throws SQLException {
        String query = "UPDATE product_categories SET name=?, description=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getId());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM product_categories WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
