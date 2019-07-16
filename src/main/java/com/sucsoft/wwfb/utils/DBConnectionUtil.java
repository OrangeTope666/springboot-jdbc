package com.sucsoft.wwfb.utils;

import java.sql.*;
import java.util.Properties;

public class DBConnectionUtil {

    private static String url = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
    private static String username = "root";
    private static String password = "root";
    private static String driverName = "oracle.jdbc.driver.OracleDriver";

    static {
        Properties properties = PropertiesUtil.getPro("application.properties");
        //获取key对应的value值
        url = properties.getProperty("dc.url");
        username = properties.getProperty("dc.username");
        password = properties.getProperty("dc.password");
        driverName = properties.getProperty("dc.driverName");
    }

    public static Connection getConnection() {
        Connection con = null;
        try {

            // 加载驱动
            Class.forName(driverName);
            // 创建一个连接
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return con;
    }

    // 关闭ResultSet
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                rs = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭Statement
    public static void closeStatement(Statement stm) {
        if (stm != null) {
            try {
                stm.close();
                stm = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭PreparedStatement
    public static void closePreparedStatement(PreparedStatement pstm) {
        if (pstm != null) {
            try {
                pstm.close();
                pstm = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭Connection
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con = null;
        }
    }
}
