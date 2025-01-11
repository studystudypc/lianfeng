package com.lianfeng.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.8
 * @注释 字段返回名字
 * @Author liuchuanping
 * @Date 2024-12-26 11:06
 */
@Data
public class DBTransmitPo implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private List<String> fieldName;
}
