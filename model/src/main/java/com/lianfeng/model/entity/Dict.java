package com.lianfeng.model.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@TableName(value ="dict")
@Data
public class Dict implements Serializable {

    @TableId(value = "dict_id", type = IdType.AUTO)
    @ExcelProperty("dict_id")
    private Integer dictId;

    @TableField(value = "state_id")
    @ExcelProperty("state_id")
    private Integer stateId;

    @TableField(value = "industry_id")
    @ExcelProperty("industry_id")
    private Integer industryId;

    @TableField(value = "company_id")
    @ExcelProperty("company_id")
    private Long companyId;

    @TableField(value = "key_words")
    @ExcelProperty("key_words")
    private String keyWords;

    @TableField(value = "vendor")
    @ExcelProperty("vendor")
    private String vendor;

    @TableField(value = "account_code")
    @ExcelProperty("account_code")
    private String accountCode;

    @TableField(value = "account")
    @ExcelProperty("account")
    private String account;

    @TableField(value = "description")
    @ExcelProperty("description")
    private String description;

    @TableField(value = "dict_order")
    @ExcelProperty("dict_order")
    private Integer dictOrder;

    @TableField(value = "beizhu")
    @ExcelProperty("beizhu")
    private String beizhu;

    @TableField(value = "key_is_word")
    @ExcelProperty("key_is_word")
    private Integer keyIsWord;

    @TableField(value = "is_approve")
    @ExcelProperty("is_approve")
    private Integer isApprove;

    @TableField(value = "dict_status")
    @ExcelProperty("dict_status")
    private Integer dictStatus;

    @TableField(value = "disabled")
    @ExcelProperty("disabled")
    private Integer disabled;

    @TableField(value = "user_id_create")
    @ExcelProperty("user_id_create")
    private Long userIdCreate;

    @TableField(value = "user_id_update")
    @ExcelProperty("user_id_update")
    private Long userIdUpdate;

    @TableField(value = "company_id_create")
    @ExcelProperty("company_id_create")
    private Long companyIdCreate;

    @TableField(value = "user_id_approve")
    @ExcelProperty("user_id_approve")
    private Long userIdApprove;

    @TableField(value = "time_create")
    @ExcelProperty("time_create")
    private Date timeCreate;

    @TableField(value = "is_auto_match")
    @ExcelProperty("is_auto_match")
    private Integer isAutoMatch;

    @TableField(value = "is_public")
    @ExcelProperty("is_public")
    private Integer isPublic;

    @TableField(value = "ask_manager")
    @ExcelProperty("ask_manager")
    private Integer askManager;

    @TableField(value = "customer_used")
    @ExcelProperty("customer_used")
    private Integer customerUsed;

    @TableField(value = "is_from_public")
    @ExcelProperty("is_from_public")
    private Integer isFromPublic;

    @TableField(value = "dict_type")
    @ExcelProperty("dict_type")
    private Integer dictType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
