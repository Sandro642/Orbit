package fr.sandro642.orbit.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:orbit_data.db";

    public void init() {
        try (Connection c = DriverManager.getConnection(URL)) {
            c.createStatement().execute("CREATE TABLE IF NOT EXISTS assets (id TEXT PRIMARY KEY, symbol TEXT, name TEXT)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
