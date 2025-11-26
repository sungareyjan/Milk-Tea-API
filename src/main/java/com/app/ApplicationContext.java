package com.app;

import com.app.migration.MigrationRunner;
import com.app.seeder.SeederRunner;

public class ApplicationContext {

    public void start() {
        MigrationRunner.run(); // Run migrations
        SeederRunner.runAll(); // Run seeder for Development and Test data

    }
}
