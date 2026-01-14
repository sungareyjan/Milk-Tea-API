package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PermissionSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {
        String[][] permissions = {
            {"create_order", "Permission to create a new order"},
            {"view_orders", "Permission to view all orders"},
            {"update_order_status", "Permission to update order status"},
            {"cancel_order", "Permission to cancel an order"},
            {"manage_users", "Permission to create/edit users"},
            {"view_reports", "Permission to view sales reports"},
            {"manage_roles", "Permission to assign roles"},
            {"manage_permissions", "Permission to edit permissions"},
            {"manage_products", "Permission to add/edit/delete products"},
            {"manage_customers", "Permission to manage customer profiles"}
        };

        String query = "INSERT IGNORE INTO permissions (name, description) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String[] perm : permissions) {
                preparedStatement.setString(1, perm[0]);
                preparedStatement.setString(2, perm[1]);
                preparedStatement.executeUpdate();
            }

            System.out.println("Permissions seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed permissions: " + e.getMessage(), e);
        }
    }
}
