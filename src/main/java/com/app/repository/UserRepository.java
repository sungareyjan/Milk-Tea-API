package com.app.repository;

import com.app.exception.DuplicateResourceException;
import com.app.model.ProductCategory;
import com.app.model.User;
import com.app.model.enums.Role;
import com.app.repository.impl.UserRepositoryImpl;
import com.app.util.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository implements UserRepositoryImpl {

    private final Connection connection;
    public UserRepository(Connection connection){this.connection = connection;}

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = """
                SELECT users.public_id, users.username, users.password, roles.name as role FROM users 
                JOIN user_roles ON users.id = user_roles.user_id
                JOIN roles ON user_roles.role_id = roles.id 
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                users.add(new User(
                    resultSet.getString("public_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    Role.valueOf(resultSet.getString("role").toUpperCase())
                ));
            }
        }

        return users;

    }

    @Override
    public User findById(String publicId) throws SQLException {
        String query = """
        SELECT users.public_id, users.username, users.password, roles.name AS role
        FROM users
        JOIN user_roles ON users.id = user_roles.user_id
        JOIN roles ON user_roles.role_id = roles.id
        WHERE users.public_id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, publicId);  // set param BEFORE executing query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = Role.valueOf(rs.getString("role").toUpperCase());
                    return new User(
                            rs.getString("public_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            role
                    );
                }
            }
        }

        return null; // user not found
    }

    @Override
    public User save(User user) throws SQLException {
        int userId;
        connection.setAutoCommit(false); // Start transaction
        String insertUser = """
            INSERT INTO users
            (public_id,username,password,email,first_name,middle_name,last_name)
            VALUES (?,?,?,?,?,?,?)
            """;

        try(PreparedStatement preparedStatement = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1,user.getPublicId());
            preparedStatement.setString(2,user.getUsername());
            preparedStatement.setString(3,user.getPassword());
            preparedStatement.setString(4,user.getEmail());
            preparedStatement.setString(5,user.getFirstName());
            preparedStatement.setString(6,user.getMiddleName());
            preparedStatement.setString(7,user.getLastName());

            preparedStatement.executeUpdate();

            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (!keys.next()) {
                throw new SQLException("Failed to retrieve user ID");
            }

            userId = keys.getInt(1);

        } catch (SQLIntegrityConstraintViolationException e) {
            // handle duplicates here
            if (e.getMessage().contains("users.username")) {
                throw new DuplicateResourceException("Username '" + user.getUsername() + "' already exists.");
            } else {
                throw e; // rethrow other constraint violations
            }
        }

        String insertRole = """
            INSERT INTO user_roles (role_id,user_id) VALUES (?,?)
            """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(insertRole,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1, user.getRoleId());
            preparedStatement.setInt(2, userId);
            preparedStatement.executeUpdate();
        }

        connection.commit();
        connection.setAutoCommit(true);
        return user;
    }

    @Override
    public void update(User user) throws SQLException {
        String query = """
        UPDATE users
        SET password = ?, first_name = ?, middle_name = ?, last_name = ?, email = ?
        WHERE public_id = ?
    """;

        connection.setAutoCommit(false);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getMiddleName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPublicId()); // important: where clause param

            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback(); // rollback on failure
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


}

