package com.lianfeng.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianfeng.common.exception.LFBusinessException;

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
}
