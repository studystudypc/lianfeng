package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存配置信息表
 * @TableName db_saveoreditinfo_table
 */
@TableName(value ="db_saveoreditinfo_table")
@Data
public class DbSaveoreditinfoTable implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 保存表字段的信息，以JSON格式存储
     */
    private String tablejson;

    /**
     * 是否传输，0表示未传输，1表示已传输
     */
    private Integer isUpdate;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 记录更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}