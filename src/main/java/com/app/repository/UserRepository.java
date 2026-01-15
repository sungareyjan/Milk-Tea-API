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
            SELECT u.public_id, u.username, u.password, u.email, u.first_name, u.middle_name, u.last_name, r.name as role, r.id as role_id
            FROM users u
            JOIN user_roles ur ON u.id = ur.user_id
            JOIN roles r ON ur.role_id = r.id
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                users.add(new User(
                    resultSet.getString("public_id"),
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("first_name"),
                    resultSet.getString("middle_name"),
                    resultSet.getString("last_name"),
                    Role.valueOf(resultSet.getString("role").toUpperCase()),
                    resultSet.getInt("role_id")
                ));
            }
        }

        return users;

    }

    @Override
    public User findUserById(String publicId) throws SQLException {
        String query = """
            SELECT u.public_id, u.username, u.password, u.email, u.first_name, u.middle_name, u.last_name,
                   r.name AS role, r.id AS role_id
            FROM users u
            JOIN user_roles ur ON u.id = ur.user_id
            JOIN roles r ON ur.role_id = r.id
            WHERE u.public_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, publicId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                        resultSet.getString("public_id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("first_name"),
                        resultSet.getString("middle_name"),
                        resultSet.getString("last_name"),
                        Role.valueOf(resultSet.getString("role").toUpperCase()),
                        resultSet.getInt("role_id")
                    );
                }
            }
        }


        return null; // user not found
    }

    @Override
    public User insertUser(User user) throws SQLException {
        int userId;
        connection.setAutoCommit(false);

        try {
            // 1️⃣ Insert user
            String insertUser = """
            INSERT INTO users
            (public_id, username, password, email, first_name, middle_name, last_name)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

            try (PreparedStatement ps = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getPublicId());
                ps.setString(2, user.getUsername());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getFirstName());
                ps.setString(6, user.getMiddleName());
                ps.setString(7, user.getLastName());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) throw new SQLException("Failed to get user ID");
                userId = keys.getInt(1);
            }

            if (user.getRoleId() != null) {
                String insertRole = "INSERT INTO user_roles (role_id, user_id) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertRole)) {
                    ps.setInt(1, user.getRoleId());
                    ps.setInt(2, userId);
                    ps.executeUpdate();
                }
            }

            connection.commit();

            populateUserRole(connection, user, userId);

            return user;

        } catch (SQLIntegrityConstraintViolationException e) {
            connection.rollback(); // rollback first
            if (e.getMessage().contains("users.username")) {
                throw new DuplicateResourceException("Username '" + user.getUsername() + "' already exists.");
            } else if (e.getMessage().contains("users.email")) {
                throw new DuplicateResourceException("Email '" + user.getEmail() + "' already exists.");
            } else {
                throw e; // rethrow other SQL constraint violations
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void updateUser(User user) throws SQLException {
        if (user.getPublicId() == null || user.getPublicId().isEmpty()) {
            throw new IllegalArgumentException("User publicId is required for update");
        }

        connection.setAutoCommit(false);

        try {
            String updateUser = """
                UPDATE users
                SET username=?, email=?, first_name=?, middle_name=?, last_name=?, password=?
                WHERE public_id=?
            """;

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateUser)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getFirstName());
                preparedStatement.setString(4, user.getMiddleName());
                preparedStatement.setString(5, user.getLastName());
                preparedStatement.setString(6, user.getPassword()); // hashed in service
                preparedStatement.setString(7, user.getPublicId());
                preparedStatement.executeUpdate();
            }

            if (user.getRoleId() != null) {
                // Get internal user id from DB
                int userId = 0;
                String getUserId = "SELECT id FROM users WHERE public_id = ?";
                try (PreparedStatement preparedStatementUser = connection.prepareStatement(getUserId)) {
                    preparedStatementUser.setString(1, user.getPublicId());
                    try (ResultSet resultSet = preparedStatementUser.executeQuery()) {
                        if (resultSet.next()) {
                            userId = resultSet.getInt("id");
                        } else {
                            throw new SQLException("User not found for publicId: " + user.getPublicId());
                        }
                    }
                }

                // Upsert role
                String upsertRole = """
                    INSERT INTO user_roles (user_id, role_id)
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE role_id = VALUES(role_id)
                """;

                try (PreparedStatement preparedStatementRole = connection.prepareStatement(upsertRole)) {
                    preparedStatementRole.setInt(1, userId);
                    preparedStatementRole.setInt(2, user.getRoleId());
                    preparedStatementRole.executeUpdate();
                }

                populateUserRole(connection, user, userId);
            }

            connection.commit();

        } catch (SQLIntegrityConstraintViolationException e) {
            connection.rollback();
            if (e.getMessage().contains("users.username")) {
                throw new DuplicateResourceException("Username '" + user.getUsername() + "' already exists.");
            } else if (e.getMessage().contains("users.email")) {
                throw new DuplicateResourceException("Email '" + user.getEmail() + "' already exists.");
            } else {
                throw e;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void populateUserRole(Connection connection, User user, int userId) throws SQLException {
        String query = """
        SELECT r.id AS role_id, r.name AS role_name
        FROM user_roles ur
        JOIN roles r ON ur.role_id = r.id
        WHERE ur.user_id = ?
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.setRoleId(resultSet.getInt("role_id"));
                user.setRoleName(resultSet.getString("role_name"));
                try {
                    user.setRole(Role.valueOf(resultSet.getString("role_name"))); // enum for route
                } catch (IllegalArgumentException e) {
                    user.setRole(null); // DB has role not in enum
                }
            }
        }
    }


}

