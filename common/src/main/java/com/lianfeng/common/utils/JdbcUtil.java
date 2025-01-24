package com.lianfeng.common.utils;

import java.sql.*;

public class JdbcUtil {

    // JDBC驱动名及数据库URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  

    static {  
        try {
            Class.forName(JDBC_DRIVER);  
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接Mysql
     * @param url
     * @param username
     * @param password
     * @return
     */
    public static Connection getConnection(String url,String username,String password) {
        Connection connection = null;
        try {
            //1、注册JDBC驱动
            Class.forName(JDBC_DRIVER);
            /* 2、获取数据库连接 */
            connection = DriverManager.getConnection(url,username,password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /*关闭结果集、数据库操作对象、数据库连接*/
    public static void release(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {

        if(resultSet!=null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(preparedStatement!=null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void release(Connection connection, ResultSet resultSet) {

        if(resultSet!=null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection!=null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
