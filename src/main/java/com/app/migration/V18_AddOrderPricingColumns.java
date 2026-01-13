package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V18_AddOrderPricingColumns implements Migration {

    @Override
    public void run(Connection connection) throws SQLException {

        String sql = """
            ALTER TABLE orders
                ADD COLUMN delivery_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER public_customer_id,
                ADD COLUMN service_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER delivery_fee,
                ADD COLUMN discount DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER service_fee;
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
