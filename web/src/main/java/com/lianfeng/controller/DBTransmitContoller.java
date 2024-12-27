package com.lianfeng.controller;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.response.R;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.service.IDBTransmitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @version 1.8
 * @注释  传输数据库模块
 * @Author liuchuanping
 * @Date 2024-12-26 10:37
 */
@Api(tags = "传输数据库模块")
@RequestMapping("DBTransmit")
@RestController
public class DBTransmitContoller {

    @Autowired
    private IDBTransmitService idbTransmitService;

    @ApiOperation(
            value = "数据库传输接口",
            notes = "指定某个数据或某个字段上传数据库",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("DBTransmit")
    public R<DBTransmitPo> dBTransmit(String tableName, String[] keyName, String[] keyValue, String[] fieldName) throws SQLException {
        if (StringUtils.isBlank(tableName)){
            throw new LFBusinessException("表名不能为空");
        }
        if (ArrayUtils.isEmpty(keyName)){
            throw new LFBusinessException("主键名称不能为空");
        }
        if (ArrayUtils.isEmpty(keyValue) && ArrayUtils.isEmpty(fieldName)){
            DBTransmitPo dbTransmitPo= idbTransmitService.dBTransmit(tableName,keyName);//全表传输
        }


        return R.success();
    }
}