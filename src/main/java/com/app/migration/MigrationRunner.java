package com.app.migration;

import com.app.database.DBConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class MigrationRunner {
    public static void run() {
        List<Migration> migrations = Arrays.asList(
                new V1_CreateRolesTable(),
                new V2_CreatePermissionsTable(),
                new V3_CreateUsersTable(),
                new V4_CreateUserRolesTable(),
                new V5_CreateRolePermissionsTable(),
                new V6_CreateCustomerTable(),
                new V7_CreateProductCategoryTable(),
                new V8_CreateProductSizeTable(),
                new V9_CreateProductTable(),
                new V10_CreateOrderTable(),
                new V11_CreateOderItemTable(),
                new V12_CreatePaymentMethodTable(),
                new V13_CreatePaymentTable(),
                new V14_CreateAuditLogTable(),
                new V15_AddUniqueConstrainToProducts(),
                new V16_CreateCustomerAddressTable(),
                new V17_AddOrderPricingColumns(),
                new V18_CreateMerchantsTable()
                // Add more here in the future
        );

        run(migrations);
    }
    public static void run(List<Migration> migrations) {
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Create migrations table if not exists , same on laravel
            statement.execute("""
            CREATE TABLE IF NOT EXISTS migrations (
                id INT AUTO_INCREMENT PRIMARY KEY,
                migration VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """);

            for (Migration migration : migrations) {
                String migrationName = migration.getClass().getSimpleName();

                // Check if migration already executed
                var resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM migrations WHERE migration = '" + migrationName + "'"
                );
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    continue; // Already executed
                }

                // Run migration
                migration.run(connection);
                System.out.println("Executed: " + migrationName);

                // Record executed migration
                statement.execute("INSERT INTO migrations (migration) VALUES ('" + migrationName + "')");
            }

        } catch (Exception e) {
            throw new RuntimeException(" Migration failed: " + e.getMessage(), e);
        }
    }
}
