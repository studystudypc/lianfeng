package com.lianfeng.common.utils;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianfeng.common.exception.LFBusinessException;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @version 1.8
 * @注释  JSON工具类
 * @Author liuchuanping
 * @Date 2024-12-14 21:29
 */
public class JsonUtiles {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @return
     * @Author liuchuaping
     * @Description 对象转换为 JSON 字符串
     * @Date 2024-12-14 22:12
     * @Param  @param obj
     * @return 实体对象
     **/
    public static String objectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new LFBusinessException("实体对象转换出错");
        }
    }

    /**
     * @return
     * @Author liuchuaping
     * @Description 对象转换为 JSON 字符串
     * @Date 2024-12-14 22:12
     * @Param  实体对象
     * @param json
     * @return json对象
     **/
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
          throw new LFBusinessException("Json格式转换出错");
        }
    }
    /**
     * 将JSON字符串转换为实体对象列表。
     *
     * @param <T> 实体对象类型
     * @param json JSON字符串
     * @param clazz 实体对象类
     * @return 实体对象列表
     */
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new LFBusinessException("Json格式转换出错");
        }
    }



}
