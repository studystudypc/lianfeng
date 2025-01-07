package com.lianfeng.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
 
import java.util.concurrent.TimeUnit;
 
/**
 * RedisService 类提供了简化的 Redis 操作接口，用于在 Spring Boot 应用中存储和检索数据。
 * 它通过 RedisTemplate 与 Redis 服务器交互，执行常见的操作如设置值、获取值、设置值带过期时间和删除值。
 */
@Service
public class RedisService {
 
    /*
        意义: RedisTemplate 是 Spring 提供的一个 Redis 操作模板，它抽象了 Redis 的底层访问，
        使开发者可以用 Java 对象操作 Redis。使用 @Autowired 注解，Spring 自动将配置好的 RedisTemplate 注入到 RedisService 类中
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
 
    // 作用: 向 Redis 中存储一个键值对
    public void setValue(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
 
    // 作用: 从 Redis 中获取指定键的值
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
 
    // 作用: 向 Redis 中存储一个键值对，并设置其过期时间
    // timeout 指定时间量，timeUnit 指定时间单位
    public void setValueWithExpiry(String key, Object value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
 
    // 作用: 从 Redis 中删除指定键及其对应的值
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}