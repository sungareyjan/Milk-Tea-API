package com.app.repository;

import com.app.model.User;
import com.app.model.enums.Role;
import com.app.repository.impl.AuthRepositoryImpl;

import java.sql.*;

public class AuthRepository implements AuthRepositoryImpl {

    private final Connection connection;

    public AuthRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        String query = """
            SELECT users.public_id, users.username, users.password, roles.name AS role
            FROM users
            JOIN user_roles ON user_roles.user_id = users.id
            JOIN roles ON roles.id = user_roles.role_id
            WHERE users.username = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                    resultSet.getString("public_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    Role.valueOf(resultSet.getString("role").toUpperCase())
                );
            }
        }
        return null;

    }

}
