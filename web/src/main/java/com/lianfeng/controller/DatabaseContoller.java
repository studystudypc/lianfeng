package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.vo.CompareDBVo;
import com.lianfeng.vo.DatabaseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Api(tags = "系统模块")
@RequestMapping("dict")
@RestController
public class DatabaseContoller {

    @Autowired
    private IDatabaseService iDatabaseService;
    @ApiOperation(
            value = "文件上传接口，",
            notes = "上传Excel文件,并存入数据库，" +
                    "file = 上传的文件，" +
                    "name = 需要上传的数据库表名，" +
                    "idName = 文件上传的主键字段" +
                    "注意事项：1.上传的Excel文件最后一列值不能为空。" ,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("uploadExcel")
    public R<DatabaseVo> uploadExcel(@RequestPart("file")  MultipartFile file, @RequestParam("name") String name,@RequestParam("idName") String idName) {
        if (file.isEmpty() || name.isEmpty()){
            return R.fail("文件上传为空或文件名字为空");
        }
        DatabaseVo databaseVo = iDatabaseService.uploadExcel(file,name,idName);
        return R.data(databaseVo);
    }

/*    @ApiOperation(
            value = "文件上传接口，",
            notes = "" ,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("updateExcel")
    public R<DatabaseVo> updateExcel(@RequestPart("file")  MultipartFile file,) {
        if (file.isEmpty() || name.isEmpty()){
            return R.fail("文件上传为空或文件名字为空");
        }
        DatabaseVo databaseVo = iDatabaseService.uploadExcel(file,name,idName);

        return R.data(databaseVo);
    }*/



    @ApiOperation(
            value = "比较数据库结构",
            notes = "比较两个数据库（源数据库和目标数据库）中某个数据表的结构是否相同," +
                    "sourceTableName = 源数据库某表名字，" +
                    "targetTableName = 目标数据库某表名字",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("compareDB")
    public R<CompareDBVo> compareDB(MultipartFile sourceFile, MultipartFile targetFile, String sourceTableName, String targetTableName){
        CompareDBVo compareDBVo = iDatabaseService.compareDB(sourceFile,targetFile,sourceTableName,targetTableName);

        return R.data(compareDBVo);
    }

    @ApiOperation(
            value = "返回字段",
            notes = "返回Excel表头字段",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("returnReverso")
    public R<DatabaseVo> returnReverso(@RequestPart("file")  MultipartFile file){
        if (file.isEmpty() ){
            return R.fail("文件上传为空或文件名字为空");
        }

        DatabaseVo databaseVo = iDatabaseService.returnReverso(file);
        return R.data(databaseVo);
    }
}
