package com.lianfeng.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @version 1.8
 * @注释  全局跨域
 * @Author liuchuanping
 * @Date 2024-12-28 17:00
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer  {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                // 这里写的*表示允许所有域，实际情况可能有以下几种形式
                // .allowedOrigins("http://app.example.com:80"); 写全了，个人认为最正规的写法，但是更喜欢用下面这种
                // .allowedOrigins("http://app.example.com"); 只写了协议和域名，端口使用http默认的80，https的话是443，应该是最常用的写法（因为一般都使用默认的端口）
                // .allowedOrigins("http://123.123.123.123:8888"); 前端服务器没有域名的情况下，也可以使用ip地址
                .allowedOrigins("*")
                // 是否允许cookie
                .allowCredentials(true)
                // 设置允许的请求方式
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                // 设置允许的header属性
                .allowedHeaders("*")
                // 跨域允许时间，用于设置预检请求（OPTIONS方法）的缓存时间（单位秒）。设置为 3600（即1小时）表示浏览器可以缓存这个CORS响应信息1小时，期间对同一源的跨域请求不再发送预检请求，直接使用缓存结果，从而提高性能。
                .maxAge(3600);
    }
}
