package com.sunrisedentalclinic.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {

    private static DBConnectionManager instance;

    private static final String DB_NAME = System.getProperty("db.name", "sunrise_dental_test");
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnectionManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static synchronized DBConnectionManager getInstance() {
        if (instance == null) {
            instance = new DBConnectionManager();
        }
        return instance;
    }

    // Returns a NEW Connection on every call. The manager itself remains a Singleton
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}