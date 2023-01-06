package com.example.utils;

import java.sql.*;

public class JDBCUtils {

    public static Connection getConnection() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8";
        String name = "root";
        String password = "zz1996";

        Connection conn = null;

        try {
            //Class.forName(driver);
            conn = DriverManager.getConnection(url, name, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 数据库查询
    public static void testQuery(Connection conn) throws SQLException {
        int selectSex = 0;
        String selectName = "mike";

        try(Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT id, name, sex, sal FROM student WHERE name = '" + selectName + "' AND sex = '" + selectSex + "'")) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String name = rs.getString(2);
                    int sex = rs.getInt(3);
                    double sal = rs.getDouble(4);
                    System.out.printf("student: id = %d, name = %s, sex = %d, sal = %f\n", id, name, sex, sal);
                }
            }
        }
    }

    // sql注入攻击
    public static void testSQLInjection(Connection conn) throws SQLException {
        String selectUserName = "mike' OR username = ";
        String selectPassword = " OR password = '"; //输入有问题的密码，也能查询到对应的用户信息

        try(Statement stmt = conn.createStatement()) {
            try(ResultSet rs = stmt.executeQuery("SELECT id, username, password FROM user WHERE username = '" + selectUserName + "' AND password = '" + selectPassword + "'")) {
                while (rs.next()) {
                    int id = rs.getInt(1);
                    String username = rs.getString(2);
                    String password = rs.getString(3);
                    System.out.printf("user: id = %d, username = %s, password = %s\n", id, username, password);
                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection conn = getConnection();
        if (conn == null || conn.isClosed()) {
            return;
        }

        testSQLInjection(conn);

    }

}
