package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.po.ConformSQLPo;
import com.lianfeng.service.IDBTransmitConditionService;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.ConditionVo;
import com.lianfeng.vo.DBNameVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.Port;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-23 9:49
 */
@Api(tags = "条件传输模块")
@RestController
public class DBTransmitConditionContoller {

    @Autowired
    private IDBTransmitConditionService idbTransmitConditionService;
    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;

    /**
     * 1、查询数据表的内容
     * 2、根据表查询数据库中字段
     * 3、把对应表名的值传入拼成sql
     */
    @ApiOperation(
            value = "条件传输模块接口",
            notes = "条件传输模块",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("transmitCondition")
    public R transmitCondition(@RequestBody ConditionVo conditionVo) throws SQLException {
        List<DBNameVo> listTable = iDbConnectionInfoService.returnTableName();//查询数据表的内容
        List<Map<String,List<Map<String, String>>>> queryField= idbTransmitConditionService.queryCondition(conditionVo,listTable);//根据表查询数据库中字段

//        ConformSQLPo conformSQLPo = idbTransmitConditionService.conformSQL(queryField);//查找出来符合条件的sql
        List<String> sqls = idbTransmitConditionService.splicingSQL(queryField);//拼接sql

        idbTransmitConditionService.transmitSQL(sqls);//执行SQL
        return R.success();
    }

}
