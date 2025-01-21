package com.lianfeng.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据传输日志表
 * @TableName data_transfer_log
 */
@TableName(value ="data_transfer_log")
@Data
public class DataTransferLogPo implements Serializable {
    /**
     * 唯一标识符，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 配置JSON，存储字段相关信息
     */
    private String field;

    /**
     * 传输状态：0表示传输失败，1表示传输成功，2表示传输中
     */
    private Integer transferStatus;

    /**
     * 传输开始时间
     */
    private Date startTime;

    /**
     * 传输结束时间
     */
    private Date endTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 日志创建时间
     */
    private Date createdAt;

    /**
     * 日志更新时间
     */
    private Date updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}