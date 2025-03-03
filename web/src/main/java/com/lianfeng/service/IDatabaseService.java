package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianfeng.vo.CompareDBVo;
import com.lianfeng.po.DatabasePo;
import org.springframework.web.multipart.MultipartFile;

public interface IDatabaseService extends IService<Object> {

    /**
     * 文件上传接口
     * @param file
     * @param name
     * @return
     */
    DatabasePo uploadExcel(MultipartFile file, String name, String idName);

    /**
     * 比较数据库结构
     * @param sourceFile
     * @param targetFile
     * @param sourceTableName
     * @param targetTableName
     * @return
     */

    /**
     * 返回字段
     * @param file
     * @return
     */
    DatabasePo returnReverso(MultipartFile file);

    /**
     * 多字段更新
     * @param file
     * @param priName
     * @param keyName
     * @param field
     * @return
     */
    DatabasePo updateExcel(MultipartFile file,String priName,String[] keyName,String[] field);

    /**
     * 多字段用户选择更新
     * @param file
     * @param tableName
     * @param keyName
     * @param field
     * @return
     */
    DatabasePo moreUpdateField(MultipartFile file, String tableName, String[] keyName, String[] field);
}