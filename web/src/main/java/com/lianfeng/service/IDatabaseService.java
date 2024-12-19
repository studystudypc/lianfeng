package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

public interface IDatabaseService extends IService<Object> {

    String uploadExcel(MultipartFile file, String name);
}