package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolePermissionSeeder implements Seeder {

    private static long getRoleId(Connection conn, String roleName) throws SQLException {
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT id FROM roles WHERE name = ? LIMIT 1"
        )) {
            preparedStatement.setString(1, roleName);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Role not found: " + roleName);
                }
                return rs.getLong("id");
            }
        }
    }

    private static long getPermissionId(Connection conn, String permName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM permissions WHERE name = ? LIMIT 1"
        )) {
            stmt.setString(1, permName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Permission not found: " + permName);
                }
                return rs.getLong("id");
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

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES (?, ?)"
        )) {

            // Give ALL permissions to admin
            try (ResultSet allPerms = connection.createStatement().executeQuery(
                    "SELECT id FROM permissions"
            )) {
                while (allPerms.next()) {
                    stmt.setLong(1, adminRoleId);
                    stmt.setLong(2, allPerms.getLong("id"));
                    stmt.executeUpdate();
                }
            }

            // Limited permissions for staff
            for (String permName : staffPermissions) {
                long permId = getPermissionId(connection, permName);
                stmt.setLong(1, staffRoleId);
                stmt.setLong(2, permId);
                stmt.executeUpdate();
            }
        }

        System.out.println("Role permissions seeded successfully.");
    }
}
