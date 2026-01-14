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
    public List<User> findAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = """
            SELECT users.public_id, users.username, users.password, roles.name as role FROM users 
            JOIN user_roles ON users.id = user_roles.user_id
            JOIN roles ON user_roles.role_id = roles.id 
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

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
    public User findUserById(String publicId) throws SQLException {
        String query = """
            SELECT users.public_id, users.username, users.password, roles.name AS role
            FROM users
            JOIN user_roles ON users.id = user_roles.user_id
            JOIN roles ON user_roles.role_id = roles.id
            WHERE users.public_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, publicId);  // set param BEFORE executing query
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Role role = Role.valueOf(resultSet.getString("role").toUpperCase());
                    return new User(
                        resultSet.getString("public_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        role
                    );
                }
            }
        }

        return null; // user not found
    }

    @Override
    public User insertUser(User user) throws SQLException {
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
    public void updateUser(User user) throws SQLException {
        String query = """
        UPDATE users
        SET password = ?, first_name = ?, middle_name = ?, last_name = ?, email = ?
        WHERE public_id = ?
    """;

        connection.setAutoCommit(false);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getMiddleName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getEmail());
            preparedStatement.setString(6, user.getPublicId()); // important: where clause param

            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback(); // rollback on failure
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


}

