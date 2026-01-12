package com.app.seed;

import com.app.seeder.Seeder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MerchantSeeder implements Seeder {

    @Override
    public void run(Connection connection) throws SQLException {
        String sql = """
            INSERT INTO merchants (public_id, name, branch, address, contact_number)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, "RJ Codes Elit Milk Tea");
            stmt.setString(3, "San Fernando, Pampanga");
            stmt.setString(4, "123 Main Street, San Fernando, Pampanga");
            stmt.setString(5, "0917-123-4567");

            int inserted = stmt.executeUpdate();
            System.out.println("Inserted " + inserted + " merchant(s).");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed merchant", e);
        }
    }
}
