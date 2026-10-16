package com.library;

import java.sql.Connection;
import com.library.util.DBConnection;

public class ConnectionTest {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {

            System.out.println("Database connected successfully!");
            System.out.println("Library Management System is ready.");

        } catch (Exception e) {

            System.out.println("Database connection failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}