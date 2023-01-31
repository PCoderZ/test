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

    // 使用 PreparedStatement 防止sql注入攻击
    public static void testPreparedStatementQuery(Connection conn) throws SQLException {
        String selectUserName1 = "mike' OR username = ";
        String selectPassword1 = " OR password = '";

        String selectUserName2 = "mike";
        String selectPassword2 = "123456";

        try(PreparedStatement ps = conn.prepareStatement("SELECT id, username, password FROM user WHERE username = ? AND password = ?")) {
            ps.setObject(1, selectUserName2);
            ps.setObject(2, selectPassword2);
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    System.out.printf("user: id = %d, username = %s, password = %s\n", id, username, password);
                }
            }
        }
    }

    // 插入数据
    public static void testInsert(Connection conn) throws SQLException {

        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO student (id, name, sex, sal) VALUES (?, ?, ?, ?)")) {
            ps.setObject(1, 100);
            ps.setObject(2, "Jack");
            ps.setObject(3, 1);
            ps.setObject(4, 12.3);
            int n = ps.executeUpdate();
            System.out.printf("student insert %d row\n", n);
        }

        // 插入并获取主键
        /*
        try(PreparedStatement ps = conn.prepareStatement("INSERT INTO student (name, sex, sal) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, "kk");
            ps.setObject(2, 0);
            ps.setObject(3, 12.23);
            int n = ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long id = rs.getLong(1);
                    System.out.printf("student update row id: %d\n", id);
                }
            }
        }

         */
    }

    // update data
    public static void testUpdate(Connection conn) throws SQLException {

        try(PreparedStatement ps = conn.prepareStatement("UPDATE student SET sal = ? WHERE id = ?")) {
            ps.setObject(1, 20.1);
            ps.setObject(2, 100);
            int n = ps.executeUpdate();
            System.out.printf("student update %d row\n", n);
        }

    }

    // delete data
    public static void testDel(Connection conn) throws SQLException {
        try(PreparedStatement ps = conn.prepareStatement("DELETE FROM student WHERE id = ?")) {
            ps.setObject(1, 100);
            int n = ps.executeUpdate();
            System.out.printf("student delete %d row\n", n);
        }
    }

    // test transaction
    public static void testTransaction(Connection conn) throws SQLException {
        int sid = 101;

        PreparedStatement ps1 = conn.prepareStatement("INSERT INTO student (id, name, sex, sal) VALUES (?, ?, ?, ?)");
        ps1.setObject(1, sid);
        ps1.setObject(2, "kangkang");
        ps1.setObject(3, 1);
        ps1.setObject(4, 121.3);

        PreparedStatement ps2 = conn.prepareStatement("UPDATE student SET sal = ? WHERE id = ?");
        ps2.setObject(1, 200);
        ps2.setObject(2, sid);

        try {
            conn.setAutoCommit(false);
            int n = ps1.executeUpdate();
            System.out.printf("Transaction insert %d row success\n", n);
            if (sid == 102) {
                throw new SQLException();
            }
            n = ps2.executeUpdate();
            System.out.printf("Transaction update %d row success\n", n);
            conn.commit();
        } catch (SQLException e) {
            System.out.printf("SQLException %s\n", e);
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public static void main(String[] args) throws SQLException {

        Connection conn;

        //Connection conn = getConnection();
        conn = JDBCUtilsConfig.getConnection();

        if (conn == null || conn.isClosed()) {
            return;
        }
        System.out.println(conn);
        testQuery(conn);

        conn = JDBCUtilsConfig.getConnection();
        System.out.println(conn);
        testQuery(conn);
    }

}
