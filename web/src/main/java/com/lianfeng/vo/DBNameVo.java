package com.lianfeng.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @version 1.8
 * @注释 返回数据库表名的参数
 * @Author liuchuanping
 * @Date 2024-12-30 10:33
 */
@Data
@ApiModel(value = "DBNameVo参数")
public class DBNameVo {

    String tableName;
}
