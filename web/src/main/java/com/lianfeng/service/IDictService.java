package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianfeng.model.entity.Dict;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author LCP
* @description 针对表【dict】的数据库操作Service
* @createDate 2024-12-12 21:30:58
*/
public interface IDictService extends IService<Dict> {

    /**
     * 文件上传接口，
     * 并把Excel数据存入数据库
     * @param file
     */
//    void uploadFile(MultipartFile file);

    /**
     * 文件检查接口
     */
    void checkFile(String absolutePath,MultipartFile file);

    /**
     * Excel上传接口，
     * 并把Excel数据存入数据库
     * @param file
     */
    String uploadExcel(MultipartFile file);
}
