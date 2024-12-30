package com.lianfeng.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-30 8:21
 */
@Data
@ApiModel(value = "DBTransmitVo参数")
public class DBTransmitVo {

    /**
     * 表名
     */
    String tableName;

    /**
     * 主键名字
     */
    String[] keyName;

    /**
     * 键值，使用List<Map>来容纳不同键值对
     */
    private List<Map<String, Object>> keyValue;

    /**
     * 其它字段
     */
    String[] fieldName;
}
