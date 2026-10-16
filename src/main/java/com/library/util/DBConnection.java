package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/library_db";

    private static final String USER =
            System.getenv("DB_USER");

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        System.out.println("DB_USER configured: " + (USER != null));
        System.out.println("DB_PASSWORD configured: " + (PASSWORD != null));

        if (USER == null || PASSWORD == null) {
            throw new SQLException(
                    "Database credentials are not configured. Restart Eclipse and check environment variables.");
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}