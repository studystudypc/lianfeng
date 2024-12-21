package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DbConnectionInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "对比数据库模块")
@RequestMapping("DB")
@RestController
public class DbConnectionInfoContoller {

    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;

    @ApiOperation(
            value = "查看数据库源信息",
            notes = "查看数据库源信息",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("list")
    public R<List> list(){
        List<DbConnectionInfo> list = iDbConnectionInfoService.list();
        return R.data(list);
    }

    @ApiOperation(
            value = "修改并保存数据库源信息",
            notes = "修改并保存数据库源信息",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("update")
    public R update(@RequestBody DbConnectionInfo dbConnectionInfo) {
        iDbConnectionInfoService.update();
        return R.success();
    }



}
