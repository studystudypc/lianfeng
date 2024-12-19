package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface IDatabaseService extends IService<Object> {

    /**
     * 文件上传接口
     * @param file
     * @param name
     * @return
     */
    String uploadExcel(MultipartFile file, String name);

    /**
     * 比较数据库结构
     * @param sourceTableName
     * @param targetTableName
     */
    void compareDB(String sourceTableName, String targetTableName);

}