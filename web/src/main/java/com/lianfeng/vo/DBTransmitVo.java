package com.lianfeng.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

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
     * 逐渐名字
     */
    String[] keyName;

    /**
     * 主键值
     */
    String[] keyValue;

    /**
     * 其它字段
     */
    String[] fieldName;
}
