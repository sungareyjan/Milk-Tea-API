//package com.app.repository;
//
//import com.app.model.Product;
//import com.app.model.ProductCategory;
//import com.app.repository.impl.ProductRepositoryImpl;
//
//import java.math.BigDecimal;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductRepository implements ProductRepositoryImpl {
//    private final Connection connection;
//
//    public ProductRepository(Connection connection){this.connection = connection;}
//    @Override
//    public List<Product> findAll() throws SQLException {
//        List<Product> products = new ArrayList<>();
//
//        String query = """
//            SELECT
//                id,
//                name,
//                description,
//                category_id,
//                size_id,
//                price,
//                availability
//            FROM products
//        """;
//
//        try (
//                PreparedStatement preparedStatement = connection.prepareStatement(query);
//                ResultSet resultSet = preparedStatement.executeQuery()
//        ) {
//            while (resultSet.next()) {
//                Product product = new Product();
//                product.setId(resultSet.getInt("id"));
//                product.setName(resultSet.getString("name"));
//                product.setDescription(resultSet.getString("description"));
//                product.setCategoryId(resultSet.getInt("category_id"));
//                product.setSizeId(resultSet.getInt("size_id"));
//                product.setPrice(resultSet.getDouble("price"));
//                product.setAvailability(resultSet.getBoolean("availability"));
//
//                products.add(product);
//            }
//        }
//
//        return products;
//
//    }
//
//    @Override
//    public Product save(Product product) throws SQLException {
//        String insertQuery = """
//            INSERT INTO products (name, price, category_id)
//            VALUES (?,?,?)
//            """;
//
//            try (PreparedStatement preparedStatement =
//                connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
//
//                preparedStatement.setString(1, product.getName());
//                preparedStatement.setBigDecimal(2, BigDecimal.valueOf(product.getPrice()));
//                preparedStatement.setInt(3, product.getCategoryId());
//
//                preparedStatement.executeUpdate();
//
//            ResultSet keys = preparedStatement.getGeneratedKeys();
//            if (keys.next()) {
//                product.setId(keys.getInt(1));
//            }
//            return product;
//        }
//    }
//
//    @Override
//    public Product findById(int id) throws SQLException {
//        String sql = "SELECT * FROM products WHERE id = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setInt(1, id);
//
//            try  (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (!resultSet.next()) {
//                    return null; // or throw NotFoundException
//                }
//
//                return new Product(
//                    resultSet.getInt("id"),
//                    resultSet.getString("name"),
//                    resultSet.getString("description"),
//                    resultSet.getInt("category_id"),
//                    resultSet.getInt("size_id"),
//                    resultSet.getDouble("price"),
//                    resultSet.getBoolean("availability")
//                );
//            }
//        }
//    }
//
//    @Override
//    public void update(Product product) throws SQLException {
//        connection.setAutoCommit(false); // Start transaction
//            String sql = """
//            UPDATE products
//            SET name = ?, price = ?, category_id = ?
//            WHERE id = ?
//            """;
//
//        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setString(1, product.getName());
//            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(product.getPrice()));
//            preparedStatement.setInt(3, product.getCategoryId());
//            preparedStatement.setInt(4, product.getId());
//            preparedStatement.executeUpdate();
//        }
//        connection.commit();
//        connection.setAutoCommit(true);
//    }
//
//}

package com.app.repository;

import com.app.exception.DuplicateResourceException;
import com.app.model.Product;
import com.app.repository.impl.ProductRepositoryImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository implements ProductRepositoryImpl {

    private final Connection connection;

    public ProductRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Product insertProduct(Product product) {
        String query = """
            INSERT INTO products
            (name, description, category_id, size_id, price, availability)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameters
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setLong(3, product.getCategoryId());
            preparedStatement.setLong(4, product.getSizeId());
            preparedStatement.setDouble(5, product.getPrice());
            preparedStatement.setBoolean(6, product.isAvailability());

            // Execute insert
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("Creating product failed, no rows affected.");
            }

            // Get generated ID
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    product.setId(resultSet.getLong(1));
                } else {
                    throw new RuntimeException("Creating product failed, no ID obtained.");
                }
            }

            return product;

        } catch (SQLIntegrityConstraintViolationException e) {
            // Handle UNIQUE constraint for name + size
            if (e.getMessage().contains("uk_products_name_size")) {
                throw new DuplicateResourceException(
                    "Product '" + product.getName() + "' with the same size already exists."
                );
            }
            throw new RuntimeException("Database constraint violation", e);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create product", e);
        }
    }

    @Override
    public Product findProductById(long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return mapRow(resultSet);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find product", e);
        }
    }

    @Override
    public List<Product> findAllProducts() {
        String sql = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                products.add(mapRow(resultSet));
            }
            return products;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to list products", e);
        }
    }

    @Override
    public Product updateProduct(long id, Product product) {
        String query = """
            UPDATE products
            SET name=?, description=?, category_id=?, size_id=?, price=?, availability=?
            WHERE id=?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setInt(3, product.getCategoryId());
            preparedStatement.setInt(4, product.getSizeId());
            preparedStatement.setDouble(5, product.getPrice());
            preparedStatement.setBoolean(6, product.isAvailability());
            preparedStatement.setLong(7, id);

            preparedStatement.executeUpdate();
            product.setId(id);
            return product;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public boolean delete(long id) {
        String query = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        return new Product(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getInt("category_id"),
            resultSet.getInt("size_id"),
            resultSet.getDouble("price"),
            resultSet.getBoolean("availability")
        );
    }
}
