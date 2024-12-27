package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.vo.CompareDBVo;
import com.lianfeng.po.DatabasePo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;


@Api(tags = "系统模块")
@RequestMapping("dict")
@RestController
public class DatabaseContoller {

    @Autowired
    private IDatabaseService iDatabaseService;

    @ApiOperation(
            value = "文件上传接口",
            notes = "上传Excel文件,并存入数据库，" +
                    "file = 上传的文件，" +
                    "name = 需要上传的数据库表名，" +
                    "idName = 文件上传的主键字段" +
                    "注意事项：1.上传的Excel文件最后一列值不能为空。",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("uploadExcel")
    public R<DatabasePo> uploadExcel(@RequestPart("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("idName") String idName) {
        if (file.isEmpty() || name.isEmpty()) {
            return R.fail("文件上传为空或文件名字为空");
        }
        DatabasePo databasePo = iDatabaseService.uploadExcel(file, name, idName);
        return R.data(databasePo);
    }

    @ApiOperation(
            value = "返回字段",
            notes = "返回Excel表头字段",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("returnRever")
    public R<DatabasePo> returnReverso(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.fail("文件上传为空或文件名字为空");
        }
        DatabasePo databasePo = iDatabaseService.returnReverso(file);
        return R.data(databasePo);
    }

  /*  @ApiOperation(
            value = "多字段更新",
            notes = "多字段更新," +
                    "file = 文件，" +
                    "tableName = 表名，" +
                    "priName = 主键名字，" +
                    "field = 多字段",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("updateField")
    public R<DatabasePo> updateField(@RequestPart("file") MultipartFile file, String tableName, String[] keyName,String[] field) {
        if (file.isEmpty()) {
            return R.fail("文件上传为空或文件名字为空");
        }
        DatabasePo databasePo = iDatabaseService.updateExcel(file, tableName, keyName , field);
        return R.data(databasePo);
    }*/

    @ApiOperation(
            value = "多字段更新",
            notes = "多字段更新," +
                    "file = 文件，" +
                    "tableName = 表名，" +
                    "priName = 多主键名字，" +
                    "field = 多字段",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("moreUpdateField")
    public R<DatabasePo> moreUpdateField(@RequestPart("file") MultipartFile file, String tableName, String[] keyName,String[] field) {
        if (file.isEmpty()) {
            return R.fail("文件上传为空或文件名字为空");
        }

        DatabasePo databasePo = iDatabaseService.moreUpdateField(file, tableName, keyName , field);
        return R.data(databasePo);
    }
}
