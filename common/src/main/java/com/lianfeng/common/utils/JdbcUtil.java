package com.lianfeng.common.utils;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JdbcUtil {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/lianfeng?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";


    static {
        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 执行查询并返回结果列表
     */
    public static <T> List<T> query(String sql, ResultSetHandler<T> resultSetHandler) {
        List<T> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultList.add(resultSetHandler.handle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 执行更新操作
     */
    public static int update(String sql) {
        int result = 0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 结果集处理器接口
     */
    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}