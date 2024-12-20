//package com.lianfeng.configuration;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
///**
// * @version 1.8
// * @注释  yml文件中的数据源信息
// * @Author liuchuanping
// * @Date 2024-12-19 14:04
// */
//
//@Component
//@Data
//@ConfigurationProperties(prefix = "primaryDataSource")
//public class TargetConfiguration {
//    private String url;
//    private String userName;
//    private String passWord;
//
//    public Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(url,userName,passWord);
//    }
//}
