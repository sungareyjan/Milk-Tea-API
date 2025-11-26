package com.app.migration;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration {
    void run(Connection connection) throws SQLException;
}
