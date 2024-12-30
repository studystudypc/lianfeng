package com.lianfeng.controller;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.response.R;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.service.IDBTransmitService;
import com.lianfeng.vo.DBTransmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

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
    @PostMapping("listTransmit")
    public R<DBTransmitPo> dbTransmitPo(@RequestBody  List<DBTransmitVo> list) throws SQLException {
        DBTransmitPo dbTransmitPo = null;
        for (DBTransmitVo dbTransmitVo : list) {
            if (StringUtils.isBlank(dbTransmitVo.getTableName())){
                throw new LFBusinessException("表名不能为空");
            }
            if (ArrayUtils.isEmpty(dbTransmitVo.getKeyName())){
                throw new LFBusinessException("主键名称不能为空");
            }
            if (ArrayUtils.isEmpty(dbTransmitVo.getKeyValue()) && ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
                dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName());//全表传输
            }
            if(ArrayUtils.isEmpty(dbTransmitVo.getKeyValue()) && !ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
                dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName(),dbTransmitVo.getFieldName());//需要更新的字段
            }
            if (ArrayUtils.isEmpty(dbTransmitVo.getFieldName()) && !ArrayUtils.isEmpty(dbTransmitVo.getKeyValue())){
                dbTransmitPo = idbTransmitService.dBTransmitKey(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName(),dbTransmitVo.getKeyValue());//需要更新的主键
            }
            if (!StringUtils.isEmpty(dbTransmitVo.getTableName()) && !ArrayUtils.isEmpty(dbTransmitVo.getKeyName()) && !ArrayUtils.isEmpty(dbTransmitVo.getKeyValue()) && !ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
                dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName(),dbTransmitVo.getKeyValue(),dbTransmitVo.getFieldName());//全字段更新
            }
        }

        return R.success();
    }


    @ApiOperation(
            value = "数据库传输接口",
            notes = "指定某个数据或某个字段上传数据库",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("Transmit")
    public R<DBTransmitPo> dBTransmit(String tableName, String[] keyName, String[] keyValue, String[] fieldName) throws SQLException {
        DBTransmitPo dbTransmitPo = null;
        if (StringUtils.isBlank(tableName)){
            throw new LFBusinessException("表名不能为空");
        }
        if (ArrayUtils.isEmpty(keyName)){
            throw new LFBusinessException("主键名称不能为空");
        }
        if (ArrayUtils.isEmpty(keyValue) && ArrayUtils.isEmpty(fieldName)){
             dbTransmitPo = idbTransmitService.dBTransmit(tableName,keyName);//全表传输
        }
        if(ArrayUtils.isEmpty(keyValue) && !ArrayUtils.isEmpty(fieldName)){
            dbTransmitPo = idbTransmitService.dBTransmit(tableName,keyName,fieldName);//需要更新的字段
        }
        if (ArrayUtils.isEmpty(fieldName) && !ArrayUtils.isEmpty(keyValue)){
            dbTransmitPo = idbTransmitService.dBTransmitKey(tableName,keyName,keyValue);//需要更新的主键
        }
        dbTransmitPo = idbTransmitService.dBTransmit(tableName,keyName,keyValue,fieldName);//全字段更新
        return R.success();
    }
}
