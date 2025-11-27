package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {
        System.out.println("➡ Seeding products...");

        Object[][] products = {
                {"Classic Pearl Milk Tea", "Black tea with milk and pearls", 1, 2, 120.00, 1},
                {"Taro Milk Tea", "Purple taro milk tea with pearls", 1, 3, 140.00, 1},
                {"Mango Fruit Tea", "Fresh mango with green tea", 2, 2, 130.00, 1},
                {"Strawberry Fruit Tea", "Strawberry fruit tea", 2, 1, 120.00, 1},
                {"Matcha Latte", "Premium matcha milk tea", 3, 2, 150.00, 1},
                {"Brown Sugar Boba Milk", "Brown sugar pearls with fresh milk", 3, 3, 160.00, 1}
        };


        String checkSql = "SELECT id FROM products WHERE name = ? LIMIT 1";

        String insertSql = """
            INSERT INTO products
            (name, description, category_id, size_id, price, availability)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        for (Object[] p : products) {

            // Check if already exists
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, (String)p[0]);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Product '" + p[0] + "' already exists. Skipping.");
                    continue;
                }
            }

            //Insert product
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                preparedStatement.setString(1, (String)p[0]);
                preparedStatement.setString(2, (String)p[1]);
                preparedStatement.setInt(3, (int)p[2]);
                preparedStatement.setInt(4, (int)p[3]);
                preparedStatement.setDouble(5, (double)p[4]);
                preparedStatement.setInt(6, (int)p[5]);
                preparedStatement.executeUpdate();

                System.out.println("Inserted product: " + p[0]);
            }
        }

    }
}
