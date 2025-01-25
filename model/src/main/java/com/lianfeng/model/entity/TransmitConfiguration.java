package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置表，用于存储配置信息
 * @TableName transmit_configuration
 */
@TableName(value ="transmit_configuration")
@Data
public class TransmitConfiguration implements Serializable {
    /**
     * 唯一标识
     */
    @TableId
    private Integer id;

    /**
     * 配置名称
     */
    private String name;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 是否启动（0:未启动，1:已启动，2:已完成，3:启动失败）
     */
    private Integer isActive;

    /**
     * 逻辑删除标志，0表示未删除，1表示已删除
     */
    private Date isDeleted;

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