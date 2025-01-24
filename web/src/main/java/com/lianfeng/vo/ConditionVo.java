package com.lianfeng.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @version 1.8
 * @注释  条件参数
 * @Author liuchuanping
 * @Date 2025-01-23 10:29
 */
@Data
@ApiModel(value = "ConditionVo参数")
public class ConditionVo implements Serializable {


    /**
     *  条件字段
     */
    private String[] field;

    /**
     * 条件的值
     */
    private String[] value;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
