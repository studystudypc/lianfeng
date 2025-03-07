package com.lianfeng.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.response.R;
import com.lianfeng.common.utils.JsonUtiles;
import com.lianfeng.model.entity.DbSaveoreditinfoTable;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.po.DbSaveOrNamePo;
import com.lianfeng.service.IDbSaveoreditinfoTableService;
import com.lianfeng.service.IDBTransmitService;
import com.lianfeng.vo.DBTransmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lianfeng.common.response.ResponseCode.PRIMARY_KEY_NOT_FOUND;
import static com.lianfeng.common.response.ResponseCode.TABLE_NAME_CANNOT_BE_NULL;

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

    @Autowired
    private IDbSaveoreditinfoTableService iDbSaveoreditinfoTableService;

    @ApiOperation(
            value = "数据库传输接口",
            notes = "指定某个数据或某个字段上传数据库",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("listTransmit")
    public R<DBTransmitPo> listTransmit(@RequestBody DBTransmitVo dbTransmitVo) throws SQLException {
        DBTransmitPo dbTransmitPo = null;
        if (StringUtils.isBlank(dbTransmitVo.getTableName())){
            throw new LFBusinessException(TABLE_NAME_CANNOT_BE_NULL.getCode(),TABLE_NAME_CANNOT_BE_NULL.getDesc());
        }
        if (ArrayUtils.isEmpty(dbTransmitVo.getKeyName())){
            throw new LFBusinessException(PRIMARY_KEY_NOT_FOUND.getCode(),PRIMARY_KEY_NOT_FOUND.getDesc());
        }
        if (CollectionUtils.isEmpty(dbTransmitVo.getKeyValue()) && ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
            dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName());//全表传输
        }
        if(CollectionUtils.isEmpty(dbTransmitVo.getKeyValue()) && !ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
            dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(),dbTransmitVo.getKeyName(),dbTransmitVo.getFieldName());//需要更新的字段
        }
        if (ArrayUtils.isEmpty(dbTransmitVo.getFieldName()) && !CollectionUtils.isEmpty(dbTransmitVo.getKeyValue())){//需要更新的主键
            List<Map<String, Object>> keyValue  = dbTransmitVo.getKeyValue();
            List<String> keyName = new ArrayList<>();
            List<String> value = new ArrayList<>();

            for (Map<String, Object> entry  : keyValue) {
                Set<String> keySet = entry.keySet();
                for (String key : keySet) {
                    keyName.add(key);
                    value.add(entry.get(key).toString());
                }
                dbTransmitPo = idbTransmitService.dBTransmitKey(dbTransmitVo.getTableName(),keyName.toArray(new String[0]),value.toArray(new String[0]));
                keyName.clear();
                value.clear();
            }
        }

        if (!StringUtils.isEmpty(dbTransmitVo.getTableName()) && !ArrayUtils.isEmpty(dbTransmitVo.getKeyName()) && !CollectionUtils.isEmpty(dbTransmitVo.getKeyValue()) && !ArrayUtils.isEmpty(dbTransmitVo.getFieldName())){
            List<Map<String, Object>> keyValue  = dbTransmitVo.getKeyValue();
            List<String> keyName = new ArrayList<>();
            List<String> value = new ArrayList<>();

            for (Map<String, Object> entry  : keyValue) {
                Set<String> keySet = entry.keySet();
                for (String key : keySet) {
                    keyName.add(key);
                    value.add(entry.get(key).toString());
                }
                dbTransmitPo = idbTransmitService.dBTransmit(dbTransmitVo.getTableName(), keyName.toArray(new String[0]),value.toArray(new String[0]), dbTransmitVo.getFieldName());//全字段更新
                keyName.clear();
                value.clear();
            }
        }

        return R.success();
    }

    @ApiOperation(
            value = "返回某个表的字段名字",
            notes = "返回某个表的字段名字",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("reField")
    public R<DBTransmitPo> reField(@RequestParam("tableName") String tableName) throws SQLException {

        DBTransmitPo dbTransmitPo = idbTransmitService.reField(tableName);
        return R.data(dbTransmitPo);
    }

    @ApiOperation(
            value = "传输模块",
            notes = "点击按钮进行传输",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("oneTransmit")
    public R<DBTransmitPo> oneTransmit(@RequestBody List<String> idDbSaveoreditinfoTable) throws SQLException {
        List<DbSaveoreditinfoTable> list = iDbSaveoreditinfoTableService.listByIds(idDbSaveoreditinfoTable);
        for (DbSaveoreditinfoTable dbSaveoreditinfoTable : list) {
            String tablejson = dbSaveoreditinfoTable.getTablejson();
            DBTransmitVo dbTransmitVo = JsonUtiles.jsonToObject(tablejson, DBTransmitVo.class);
            listTransmit(dbTransmitVo);
        }
        return R.success("传输成功");
    }

    @ApiOperation(
            value = "数据库传输接口",
            notes = "批量指定某个数据或某个字段上传数据库",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("listsTransmit")
    public R<List<DBTransmitPo>> listTransmit(@RequestBody List<DBTransmitVo> list) throws SQLException {
        List<DBTransmitPo> dbTransmitPos = new ArrayList<>();
        for (DBTransmitVo dbTransmitVo : list) {
            R<DBTransmitPo> dbTransmitPoR = listTransmit(dbTransmitVo);
            dbTransmitPos.add(dbTransmitPoR.getData());
        }
        return R.success();

    }

}
