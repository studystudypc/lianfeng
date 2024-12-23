package com.lianfeng.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lianfeng.common.response.R;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.po.CompareTablePo;
import com.lianfeng.po.DbConnectionInfoPo;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DbConnectionInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
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
            value = "保存或更新数据库源信息",
            notes = "保存或更新数据库源信息",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("update")
    public R update(@RequestBody DbConnectionInfoVo dbConnectionInfoVo) {
        iDbConnectionInfoService.saveOrUpdateApartment(dbConnectionInfoVo);
        return R.success();
    }

    @ApiOperation(
            value = "对比表结构",
            notes = "对比表结构" +
                    "tableName = 表名",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("compareTable")
    public R compareTable(@RequestParam String tableName) throws SQLException {
        CompareTablePo compareTablePo = iDbConnectionInfoService.compareTable(tableName);
        if (compareTablePo.getDifferencesList() != null){
            return R.data(compareTablePo);
        }
        return R.success("表结构相同");
    }

}
