package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.service.IDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @version 1.8
 * @注释  Dict模块的控制器实体
 * @Author liuchuanping
 * @Date 2024-12-14 22:19
 */
@Api(tags = "Dict模块")
@RequestMapping("dict")
@RestController
public class DictContoller {

    @Autowired
    private IDictService iDictService;

    @ApiOperation(
            value = "文件上传接口",
            notes = "上传Excel文件,并存入数据库",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("uploadFile")
    public R<String> uploadFile(@RequestPart("file")  MultipartFile file) {
        iDictService.uploadFile(file);
        return R.success("文件上传成功");
    }

    @ApiOperation(
            value = "文件检查接口",
            notes = "检查上传的文件是否与数据库值一样",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("checkFile")
    public R<String> checkFile(@RequestPart("file") MultipartFile file){
        iDictService.checkFile(file);
        return R.success("文件上传无误");
    }


}
