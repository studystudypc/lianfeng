package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianfeng.vo.CompareDBVo;
import com.lianfeng.vo.DatabaseVo;
import org.springframework.web.multipart.MultipartFile;

public interface IDatabaseService extends IService<Object> {

    /**
     * 文件上传接口
     * @param file
     * @param name
     * @return
     */
    DatabaseVo uploadExcel(MultipartFile file, String name, String idName);

    /**
     * 比较数据库结构
     * @param sourceFile
     * @param targetFile
     * @param sourceTableName
     * @param targetTableName
     * @return
     */
    CompareDBVo compareDB(MultipartFile sourceFile, MultipartFile targetFile, String sourceTableName, String targetTableName);

    /**
     * 返回字段
     * @param file
     * @return
     */
    DatabaseVo returnReverso(MultipartFile file);
}