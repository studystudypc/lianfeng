package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 变量明细表，用于存储每个变量的详细数据
 * @TableName variable_details
 */
@TableName(value ="variable_details")
@Data
public class VariableDetails implements Serializable {
    /**
     * 唯一标识
     */
    @TableId
    private Integer id;

    /**
     * 关联的变量ID
     */
    private Integer configVariableId;

    /**
     * 字段名
     */
    private String field;

    /**
     * 操作符（如=, >=, <=）
     */
    private String operator;

    /**
     * 字段值
     */
    private String value;

    /**
     * 逻辑删除标志，0表示未删除，1表示已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}