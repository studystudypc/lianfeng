package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置变量表，用于存储配置相关变量信息
 * @TableName config_variable
 */
@TableName(value ="config_variable")
@Data
public class ConfigVariable implements Serializable {
    /**
     * 唯一标识
     */
    @TableId
    private Integer id;

    /**
     * 关联的配置ID
     */
    private Integer transmitConfigurationId;

    /**
     * 表名名称
     */
    private String name;

    /**
     * 变量描述
     */
    private String description;

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