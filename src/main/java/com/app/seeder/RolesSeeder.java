package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RolesSeeder implements Seeder{

//    @Override
//    public void seed() {
//
//        String[][] roles = {
//            {"admin", "Administrator with full system access"},
//            {"staff", "Employee with limited access"}
//        };
//
//        String sql = "INSERT IGNORE INTO roles (name, description) VALUES (?, ?)";
//
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            for (String[] role : roles) {
//                stmt.setString(1, role[0]);
//                stmt.setString(2, role[1]);
//                stmt.executeUpdate();
//            }
//
//            System.out.println("Roles seeded.");
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to seed roles: " + e.getMessage(), e);
//        }
//    }

    @Override
    public void run(Connection connection) throws SQLException {
        String[][] roles = {
                {"admin", "Administrator with full system access"},
                {"staff", "Employee with limited access"}
        };

        String sql = "INSERT IGNORE INTO roles (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (String[] role : roles) {
                stmt.setString(1, role[0]);
                stmt.setString(2, role[1]);
                stmt.executeUpdate();
            }

            System.out.println("Roles seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed roles: " + e.getMessage(), e);
        }
    }

}
