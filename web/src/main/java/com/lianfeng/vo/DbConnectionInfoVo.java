package com.lianfeng.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库连接信息表
 * @TableName db_connection_info
 */
@TableName(value ="db_connection_info")
@Data
public class DbConnectionInfoVo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String dbUrl;

    /**
     * 
     */
    private String dbUsername;

    /**
     * 
     */
    private String dbPassword;

    /**
     * 
     */
    private String dbDriver;

    /**
     * 记录创建时间
     */
    private Date createdAt;

    /**
     * 记录更新时间，更新记录时自动设置为当前时间
     */
    private Date updatedAt;

    /**
     * 逻辑删除标志，1表示已删除，0表示未删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}