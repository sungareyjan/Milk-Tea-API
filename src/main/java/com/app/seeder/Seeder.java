package com.app.seeder;

import java.sql.Connection;
import java.sql.SQLException;

public interface Seeder {
    void run(Connection connection) throws SQLException;
}
