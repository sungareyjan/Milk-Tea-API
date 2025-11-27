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
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                //  if admin already exists
                userId = rs.getLong("id");
                System.out.println("Admin user already exists. Skipping user insert.");
            } else {
                //Insert admin since not found
                String insertSql = """
                    INSERT INTO users
                    (public_id, username, password, first_name, last_name, email)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement stmt = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    String rawPassword = "milktea";
                    String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
                    String publicId = UUID.randomUUID().toString();

                    stmt.setString(1, publicId);
                    stmt.setString(2, "admin");
                    stmt.setString(3, hashedPassword);
                    stmt.setString(4, "System");
                    stmt.setString(5, "Administrator");
                    stmt.setString(6, "admin@milktea.com");
                    stmt.executeUpdate();

                    ResultSet keys = stmt.getGeneratedKeys();
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
        String roleSql = "SELECT id FROM roles WHERE name = 'admin' LIMIT 1";
        long roleId;
        try (PreparedStatement roleStmt = connection.prepareStatement(roleSql)) {
            ResultSet rs = roleStmt.executeQuery();
            if (rs.next()) {
                roleId = rs.getLong("id");
            } else {
                throw new RuntimeException("Admin role not found. Please seed roles first.");
            }
        }

        // Insert into user_roles
        String userRoleSql = "INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement urStmt = connection.prepareStatement(userRoleSql)) {
            urStmt.setLong(1, userId);
            urStmt.setLong(2, roleId);
            urStmt.executeUpdate();
            System.out.println("Admin user assigned to admin role.");
        }


    }
}
