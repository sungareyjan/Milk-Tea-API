package com.app.seed;

import com.app.seeder.Seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MerchantSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {
        String query = """
            INSERT INTO merchants (public_id, name, branch, address, contact_number)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, UUID.randomUUID().toString());
            preparedStatement.setString(2, "RJ Codes Elit Milk Tea");
            preparedStatement.setString(3, "San Fernando, Pampanga");
            preparedStatement.setString(4, "123 Main Street, San Fernando, Pampanga");
            preparedStatement.setString(5, "0917-123-4567");

            int inserted = preparedStatement.executeUpdate();
            System.out.println("Inserted " + inserted + " merchant(s).");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed merchant", e);
        }
    }
}
