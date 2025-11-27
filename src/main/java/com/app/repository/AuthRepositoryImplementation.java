package com.app.repository;

import com.app.model.User;

import java.sql.Connection;
import  java.sql.SQLException;
import  java.sql.PreparedStatement;
import  java.sql.ResultSet;

public class AuthRepositoryImplementation implements AuthRepository {

    private final Connection connection;

    public AuthRepositoryImplementation(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        String query = """
                SELECT users.public_id, users.username, users.password, roles.name as role
                FROM users
                JOIN user_roles ON user_roles.user_id = users.id
                JOIN roles ON roles.id = user_roles.user_id
                WHERE users.username = ?
                """
              ;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                    resultSet.getString("public_id"),
                    resultSet.getString("username"),
                    resultSet.getString("password"),
                    resultSet.getString("role")
                );
            }
        }
        return null;
    }

}
