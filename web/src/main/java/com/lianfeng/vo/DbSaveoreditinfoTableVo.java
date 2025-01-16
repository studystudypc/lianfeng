package com.lianfeng.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存配置信息表
 * @TableName db_saveoreditinfo_table
 */
@ApiModel(value = "DbSaveoreditinfoTableVo参数")
@Data
public class DbSaveoreditinfoTableVo {
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

}