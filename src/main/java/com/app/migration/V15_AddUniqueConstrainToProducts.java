package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class V15_AddUniqueConstrainToProducts implements Migration{
    @Override
    public void run(Connection connection) throws SQLException {

        String query = """
            ALTER TABLE products
            ADD CONSTRAINT uk_products_name_size
            UNIQUE (name, size_id);
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }
}
