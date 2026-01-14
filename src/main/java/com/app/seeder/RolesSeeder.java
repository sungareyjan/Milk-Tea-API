package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RolesSeeder implements Seeder{

    @Override
    public void run(Connection connection) throws SQLException {
        String[][] roles = {
            {"admin", "Administrator with full system access"},
            {"staff", "Employee with limited access"}
        };

        String query = "INSERT IGNORE INTO roles (name, description) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String[] role : roles) {
                preparedStatement.setString(1, role[0]);
                preparedStatement.setString(2, role[1]);
                preparedStatement.executeUpdate();
            }

            System.out.println("Roles seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed roles: " + e.getMessage(), e);
        }
    }

}
