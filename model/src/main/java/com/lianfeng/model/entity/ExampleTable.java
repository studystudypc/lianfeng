package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库常见表信息
 *
 * @TableName example_table
 */
@TableName(value = "example_table")
@Data
public class ExampleTable implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段名字
     */
    private String dbField;

    /**
     * 字段类型
     */
    private String dbType;

    /**
     * 字段是否为空
     */
    private String dbNull;

    /**
     * 主键信息
     */
    private String dbKey;

    /**
     * 默认值
     */
    private String dbDefault;

    /**
     * 额外信息
     */
    private String dbExtra;

    /**
     * 备注信息
     */
    private String remarks;

    /**
     * 逻辑删除标志，0表示未删除，1表示已删除
     */
    private Integer isDeleted;

    /**
     * 创建日期
     */
    private Date createdAt;

    /**
     * 修改日期
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}