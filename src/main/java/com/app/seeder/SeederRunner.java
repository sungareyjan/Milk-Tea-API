package com.app.seeder;

import com.app.database.DBConnection;
import com.app.seed.MerchantSeeder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class SeederRunner {

    public static void runAll() {
        try (Connection connection = DBConnection.getConnection()) {
            List<Seeder> seeders = Arrays.asList(
                new RolesSeeder(),
                new PermissionSeeder(),
                new PaymentMethodSeeder(),
                new DefaultAdminUserSeeder(),
                new ProductSizeSeeder(),
                new ProductCategorySeeder(),
                new ProductSeeder(),
                new RolePermissionSeeder(),
                new MerchantSeeder()
            );

            for (Seeder seeder : seeders) {
                seeder.run(connection); // or seeder.seed(connection) if you rename
                System.out.println(seeder.getClass().getSimpleName() + " seeded.");
            }

            System.out.println("All seeders completed.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
