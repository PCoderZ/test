package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCUtilsConfig {

    private static Connection conn;
    private static String driverClass;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            readConfig();
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("mysql conn failed.");
        }
    }

    private static void readConfig() throws IOException {
        String configFile = "database.properties";
        InputStream in = JDBCUtilsConfig.class.getClassLoader().getResourceAsStream(configFile);
        Properties properties = new Properties();
        properties.load(in);
        driverClass = properties.getProperty("driverClass");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        System.out.printf("driverClass: %s, url: %s, username: %s, password: %s", driverClass, url, username, password);
    }

    public static Connection getConnection() {
        return conn;
    }

}
