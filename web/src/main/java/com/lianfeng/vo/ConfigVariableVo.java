package com.lianfeng.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lianfeng.model.entity.ConfigVariable;
import com.lianfeng.model.entity.TransmitConfiguration;
import com.lianfeng.model.entity.VariableDetails;
import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-25 15:06
 */
@Data
public class ConfigVariableVo implements Serializable {

    ConfigVariable configVariable;

    TransmitConfiguration transmitConfiguration;

    VariableDetails variableDetails;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
