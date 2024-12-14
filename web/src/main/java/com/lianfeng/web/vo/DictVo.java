package com.lianfeng.web.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-14 22:18
 */
@Data
@ApiModel(value = "Dict参数")
public class DictVo {
    /**
     * 字典id
     */
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Integer dictId;

    /**
     * 州id
     */
    @TableField(value = "state_id")
    private Integer stateId;

    /**
     * 行业id
     */
    @TableField(value = "industry_id")
    private Integer industryId;

    /**
     * 客户公司id
     */
    @TableField(value = "company_id")
    private Long companyId;

    /**
     * 关键词
     */
    @TableField(value = "key_words")
    private String keyWords;

    /**
     * 供应商
     */
    @TableField(value = "vendor")
    private String vendor;

    /**
     * 科目编码
     */
    @TableField(value = "account_code")
    private String accountCode;

    /**
     * 会计科目
     */
    @TableField(value = "account")
    private String account;

    /**
     * 描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 字典排序
     */
    @TableField(value = "dict_order")
    private Integer dictOrder;

    /**
     * 备注
     */
    @TableField(value = "beizhu")
    private String beizhu;

    /**
     * 关键词是否为单词
     */
    @TableField(value = "key_is_word")
    private Integer keyIsWord;

    /**
     * 是否审核
     */
    @TableField(value = "is_approve")
    private Integer isApprove;

    /**
     * 字典状态
     */
    @TableField(value = "dict_status")
    private Integer dictStatus;

    /**
     * 是否禁用
     */
    @TableField(value = "disabled")
    private Integer disabled;

    /**
     * 创建人id
     */
    @TableField(value = "user_id_create")
    private Long userIdCreate;

    /**
     * 修订人
     */
    @TableField(value = "user_id_update")
    private Long userIdUpdate;

    /**
     * 公司id
     */
    @TableField(value = "company_id_create")
    private Long companyIdCreate;

    /**
     * 审核人id
     */
    @TableField(value = "user_id_approve")
    private Long userIdApprove;

    /**
     * 创建时间
     */
    @TableField(value = "time_create")
    private Date timeCreate;

    /**
     * 是否自动匹配
     */
    @TableField(value = "is_auto_match")
    private Integer isAutoMatch;

    /**
     * 通用
     */
    @TableField(value = "is_public")
    private Integer isPublic;

    /**
     *
     */
    @TableField(value = "ask_manager")
    private Integer askManager;

    /**
     *
     */
    @TableField(value = "customer_used")
    private Integer customerUsed;

    /**
     *
     */
    @TableField(value = "is_from_public")
    private Integer isFromPublic;

    /**
     * 字典类型
     */
    @TableField(value = "dict_type")
    private Integer dictType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
