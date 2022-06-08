package com.example.reweb.ulti;

import com.example.reweb.ulti.Config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    private static Connection connection;

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            Class.forName(Config.DB_DRIVER_CARD);
            connection = DriverManager.getConnection(
                    Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD
            );
            System.out.println("Success!");
        } else {
            System.out.println("Using!");
        }


        return connection;
    }
}
