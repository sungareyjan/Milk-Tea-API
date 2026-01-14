package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class DefaultAdminUserSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {

        String checkSql = "SELECT id FROM users WHERE username = 'admin' LIMIT 1";
        long userId;
        try (PreparedStatement checkPreparedStatement = connection.prepareStatement(checkSql)) {
            ResultSet resultSet = checkPreparedStatement.executeQuery();
            if (resultSet.next()) {
                //  if admin already exists
                userId = resultSet.getLong("id");
                System.out.println("Admin user already exists. Skipping user insert.");
            } else {
                //Insert admin since not found
                String insertSql = """
                    INSERT INTO users
                    (public_id, username, password, first_name, last_name, email)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    String rawPassword = "milktea";
                    String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
                    String publicId = UUID.randomUUID().toString();

                    preparedStatement.setString(1, publicId);
                    preparedStatement.setString(2, "admin");
                    preparedStatement.setString(3, hashedPassword);
                    preparedStatement.setString(4, "System");
                    preparedStatement.setString(5, "Administrator");
                    preparedStatement.setString(6, "admin@milktea.com");
                    preparedStatement.executeUpdate();

                    ResultSet keys = preparedStatement.getGeneratedKeys();
                    if (keys.next()) {
                        userId = keys.getLong(1);
                    } else {
                        throw new RuntimeException("Failed to retrieve admin user ID.");
                    }

                    System.out.println("Default admin user inserted successfully.");
                }
            }
        }

        // Get role_id for 'admin'
        String roleQuery = "SELECT id FROM roles WHERE name = 'admin' LIMIT 1";
        long roleId;
        try (PreparedStatement rolePreparedStatement = connection.prepareStatement(roleQuery)) {
            ResultSet resultSet = rolePreparedStatement.executeQuery();
            if (resultSet.next()) {
                roleId = resultSet.getLong("id");
            } else {
                throw new RuntimeException("Admin role not found. Please seed roles first.");
            }
        }

        // Insert into user_roles
        String userRoleSql = "INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement userRolePreparedStatement = connection.prepareStatement(userRoleSql)) {
            userRolePreparedStatement.setLong(1, userId);
            userRolePreparedStatement.setLong(2, roleId);
            userRolePreparedStatement.executeUpdate();
            System.out.println("Admin user assigned to admin role.");
        }


    }
}
