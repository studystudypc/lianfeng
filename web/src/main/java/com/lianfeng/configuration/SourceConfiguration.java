//package com.lianfeng.configuration;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
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
//@Component
//@Configuration
//@Primary
//@ConfigurationProperties(prefix = "spring.datasource.sourceTableName")
//@Data
//public class SourceConfiguration {
//
//    private String url;
//
//    private String userName;
//
//    private String passWord;
//
//    public Connection getConnection() throws SQLException{
//        return DriverManager.getConnection(url,userName,passWord);
//    }
//}
