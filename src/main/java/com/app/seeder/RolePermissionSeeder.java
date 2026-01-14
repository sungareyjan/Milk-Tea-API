package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolePermissionSeeder implements Seeder {

    private static long getRoleId(Connection connection, String roleName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM roles WHERE name = ? LIMIT 1"
        )) {
            preparedStatement.setString(1, roleName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RuntimeException("Role not found: " + roleName);
                }
                return resultSet.getLong("id");
            }
        }
    }

    private static long getPermissionId(Connection connection, String permName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT id FROM permissions WHERE name = ? LIMIT 1"
        )) {
            preparedStatement.setString(1, permName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new RuntimeException("Permission not found: " + permName);
                }
                return resultSet.getLong("id");
            }
        }
    }

    @Override
    public void run(Connection connection) throws SQLException {

        System.out.println("Seeding role permissions...");

        long adminRoleId = getRoleId(connection, "admin");
        long staffRoleId = getRoleId(connection, "staff");

        // Staff allowed permissions only
        String[] staffPermissions = {
            "create_order",
            "view_orders",
            "update_order_status"
        };

        try (PreparedStatement preparedStatement = connection.prepareStatement(
        "INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (?, ?)"
        )) {

            // Give ALL permissions to admin
            try (ResultSet resultSet = connection.createStatement().executeQuery(
                "SELECT id FROM permissions"
            )) {
                while (resultSet.next()) {
                    preparedStatement.setLong(1, adminRoleId);
                    preparedStatement.setLong(2, resultSet.getLong("id"));
                    preparedStatement.executeUpdate();
                }
            }

            // Limited permissions for staff
            for (String permName : staffPermissions) {
                long permId = getPermissionId(connection, permName);
                preparedStatement.setLong(1, staffRoleId);
                preparedStatement.setLong(2, permId);
                preparedStatement.executeUpdate();
            }
        }

        System.out.println("Role permissions seeded successfully.");
    }
}
