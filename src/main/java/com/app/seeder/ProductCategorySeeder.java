package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductCategorySeeder implements Seeder{

    @Override
    public void run(Connection connection) throws SQLException {
        String[][] categories = {
            {"Classic Milk Tea", "Traditional milk tea flavors"},
            {"Fruit Tea", "Fresh fruit-infused teas"},
            {"Specialty", "Premium signature drinks"}
        };

        String query = "INSERT IGNORE INTO product_categories (name, description) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String[] perm : categories) {
                preparedStatement.setString(1, perm[0]);
                preparedStatement.setString(2, perm[1]);
                preparedStatement.executeUpdate();
            }

            System.out.println("Categories seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed permissions: " + e.getMessage(), e);
        }
    }
}
