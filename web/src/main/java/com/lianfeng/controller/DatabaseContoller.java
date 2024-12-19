package com.lianfeng.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.response.R;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.listenner.NoModelDataListener;
import com.lianfeng.mapper.DatabaseMapper;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.service.IDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;


@Api(tags = "系统模块")
@RequestMapping("dict")
@RestController
public class DatabaseContoller {

    @Autowired
    private IDictService iDictService;

    @Autowired
    private IDatabaseService iDatabaseService;
    @ApiOperation(
            value = "文件上传接口，",
            notes = "上传Excel文件,并存入数据库，" +
                    "file = 上传的文件，" +
                    "name = 需要上传的数据库表名，" +
                    "注意事项：1.上传的Excel文件最后一列值不能为空。" ,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("uploadExcel")
    public R<String> uploadExcel(@RequestPart("file")  MultipartFile file, String name) {
        String absolutePath = iDatabaseService.uploadExcel(file,name);
//        iDatabaseService.checkFile(absolutePath,file);
        return R.success("文件上传成功");
    }
}
