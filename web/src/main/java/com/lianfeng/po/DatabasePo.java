package com.lianfeng.po;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @version 1.8
 * @注释 Database返回对象
 * @Author liuchuanping
 * @Date 2024-12-20 10:52
 */
@Data
@ApiModel(value = "Database参数")
public class DatabasePo {

    /**
     * 文件路径
     */
    private String absolutePath;

    /**
     * 存放读取excel文件内容
     */
    private List<String[]> excelContent;

    /**
     * 存放读取excel文件行
     */
    private Integer numExcel;

    /**
     * 成功存入有主键的数据数量
     */
    private Integer numValue;

    /**
     * 成功存入没有主键的数据数量
     */
    private Integer nullValue;

    /**
     * 成功更新数据库总量
     */
    private Integer sumNums;

    /**
     * Excel表头
     */
    private String[] reversoName;

    /**
     * 存放读取空excel文件内容
     */
    private List<String[]> nullExcelContent;



}
