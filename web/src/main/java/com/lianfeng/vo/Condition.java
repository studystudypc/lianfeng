package com.lianfeng.vo;

import lombok.Data;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-24 14:43
 */
@Data
public class Condition {

    private String field;
    private String operator;
    private String value;
}
