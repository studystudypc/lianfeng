package com.lianfeng.vo;

import lombok.Data;

import java.util.List;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-24 14:42
 */
@Data
public class ConditionsVO {

    private String tableName;

    private List<Condition> condition;
}
