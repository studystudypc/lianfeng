package com.lianfeng.po;

import lombok.Data;

import java.util.List;

/**
 * @version 1.8
 * @注释  对比表的返回信息
 * @Author liuchuanping
 * @Date 2024-12-21 12:09
 */
@Data
public class CompareTablePo {

    /**
     * 表名
     */
    String tableName;
    /**
     * 不同表的数据
     */
    List<String> differencesList;

    /**
     * 日志路径
     */
    String logPath;

}
