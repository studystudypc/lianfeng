package com.lianfeng.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName account
 */
@TableName(value ="account")
@Data
public class Account implements Serializable {
    /**
     * 
     */
    @TableId(value = "account_id", type = IdType.AUTO)
    private Integer accountId;

    /**
     * 
     */
    @TableField(value = "account_code")
    private String accountCode;

    /**
     * 
     */
    @TableField(value = "account_name")
    private String accountName;

    /**
     * 
     */
    @TableField(value = "account_des")
    private String accountDes;

    /**
     * 
     */
    @TableField(value = "calculate")
    private Integer calculate;

    /**
     * 常用科目
     */
    @TableField(value = "assistant_use1")
    private Integer assistantUse1;

    /**
     * 助手用
     */
    @TableField(value = "assistant_use")
    private Integer assistantUse;

    /**
     * 非常用科目
     */
    @TableField(value = "assistant_use2")
    private Integer assistantUse2;

    /**
     * 会计科目分类
     */
    @TableField(value = "account_class")
    private String accountClass;

    /**
     * 客户用
     */
    @TableField(value = "customer_use")
    private Integer customerUse;

    /**
     * 
     */
    @TableField(value = "drake")
    private String drake;

    /**
     * 是否禁用
     */
    @TableField(value = "disabled")
    private Integer disabled;

    /**
     * trial_blance_id
     */
    @TableField(value = "trial_blance_id")
    private Integer trialBlanceId;

    /**
     * 备注
     */
    @TableField(value = "demo")
    private String demo;

    /**
     * 是否为资产
     */
    @TableField(value = "is_asset")
    private Integer isAsset;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}