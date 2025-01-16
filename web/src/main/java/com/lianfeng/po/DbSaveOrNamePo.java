package com.lianfeng.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-16 12:56
 */

@ApiModel(value = "拼接保存传输字段的配置内容")
@Data
public class DbSaveOrNamePo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 保存表字段的信息，以JSON格式存储
     */
    private String tablejson;

    /**
     * 是否传输
     */
    private Integer isupdate;
}
