package com.app.seeder;

import java.sql.Connection;
import java.sql.SQLException;

public interface Seeder {
//    void seed();
    void run(Connection connection) throws SQLException;
//    run(Connection connection) throws SQLException;
}
