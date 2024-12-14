package com.lianfeng.web;

import com.lianfeng.common.constants.LFanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @version 1.8
 * @注释  项目启动类
 * @Author liuchuanping
 * @Date 2024-12-12 18:29
 */
@SpringBootApplication(scanBasePackages = LFanConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = LFanConstants.BASE_COMPONENT_SCAN_PATH)
@EnableCaching//缓存注解
@EnableAsync//异步注解
@MapperScan("com.lianfeng.web.mapper")
@EnableTransactionManagement
public class lianfengWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(lianfengWebApplication.class);
    }
}
