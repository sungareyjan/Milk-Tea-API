package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductSizeSeeder implements Seeder{

    @Override
    public void run(Connection connection) throws SQLException {
        String[][] size = {
                {"Small", "250ml"},
                {"Medium", "350ml"},
                {"Large", "500ml"}
        };

        String sql = "INSERT IGNORE INTO product_sizes (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (String[] perm : size) {
                stmt.setString(1, perm[0]);
                stmt.setString(2, perm[1]);
                stmt.executeUpdate();
            }

            System.out.println("Size seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed permissions: " + e.getMessage(), e);
        }
    }
}
