package com.app.seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentMethodSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {

        String[][] methods = {
                {"Cash", "Cash transaction"},
                {"Maya", "Maya e-wallet"},
                {"GCash", "GCash digital payment"},
                {"Card", "Credit/Debit card payment"},
        };

        String query = "INSERT IGNORE INTO payment_methods (name, description) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            for (String[] method : methods) {
                preparedStatement.setString(1, method[0]);
                preparedStatement.setString(2, method[1]);
                preparedStatement.executeUpdate();
            }

            System.out.println("Payment methods seeded.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to seed payment methods: " + e.getMessage(), e);
        }
    }
}
