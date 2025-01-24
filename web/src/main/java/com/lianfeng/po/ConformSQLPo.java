package com.lianfeng.po;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @version 1.8
 * @注释 条件查询中符合条件的sql和不符合条件的sql字段放在这里
 * @Author liuchuanping
 * @Date 2025-01-24 8:55
 */
@Data
public class ConformSQLPo {

    /**
     * 符合条件的
     */
    List<Map<String,List<Map<String, String>>>> conform;

    /**
     * 不符合的条件
     */
    List<Map<String,List<Map<String, String>>>> isConform;
}
