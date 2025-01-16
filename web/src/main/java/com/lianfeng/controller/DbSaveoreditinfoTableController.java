package com.lianfeng.controller;

import com.lianfeng.common.response.R;
import com.lianfeng.common.utils.JsonUtiles;
import com.lianfeng.model.entity.DbSaveoreditinfoTable;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.po.DbSaveOrNamePo;
import com.lianfeng.service.DbSaveoreditinfoTableService;
import com.lianfeng.service.IDBTransmitService;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DBNameVo;
import com.lianfeng.vo.DBTransmitVo;
import com.lianfeng.vo.DbSaveoreditinfoTableVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "配置传输字段模块")
@RequestMapping("TransmitSaveOrEdit")
@RestController
public class DbSaveoreditinfoTableController{

    @Autowired
    private DbSaveoreditinfoTableService dbSaveoreditinfoTableService;
    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;


    @ApiOperation(
            value = "保存和编辑模块",
            notes = "指定某个表的需要配置的东西",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("saveOrEdit")
    public R saveOrEdit(@RequestBody DbSaveoreditinfoTableVo dbSaveoreditinfoTableVo){
        DbSaveoreditinfoTable dbSaveoreditinfoTable = new DbSaveoreditinfoTable();
        BeanUtils.copyProperties(dbSaveoreditinfoTableVo,dbSaveoreditinfoTable);
        dbSaveoreditinfoTableService.saveOrUpdate(dbSaveoreditinfoTable);

        return R.success("保存成功");
    }

    @ApiOperation(
            value = "查看配置传输字段模块",
            notes = "查看配置传输字段模块",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("list")
    public R<List<DbSaveOrNamePo>> list() throws SQLException {
        List<DbSaveoreditinfoTable> list = dbSaveoreditinfoTableService.list();
        List<DBNameVo> listTableName = iDbConnectionInfoService.returnTableName();
        List<DbSaveOrNamePo> result = new ArrayList<>();

        Map<String, DbSaveOrNamePo> tableMap = new HashMap<>();

        for (DBNameVo tableNameVo : listTableName) {
            DbSaveOrNamePo dbSaveOrNamePo = new DbSaveOrNamePo();
            dbSaveOrNamePo.setTableName(tableNameVo.getTableName());
            dbSaveOrNamePo.setTablejson("未配置");
            dbSaveOrNamePo.setIsupdate(0);
            tableMap.put(tableNameVo.getTableName(), dbSaveOrNamePo);
        }

        for (DbSaveoreditinfoTable dbSaveoreditinfoTable : list) {
            String tablejson = dbSaveoreditinfoTable.getTablejson();
            DBTransmitVo dbTransmitVo = JsonUtiles.jsonToObject(tablejson, DBTransmitVo.class);
            String tableName = dbTransmitVo.getTableName();
            if (tableMap.containsKey(tableName)) {
                DbSaveOrNamePo dbSaveOrNamePo = tableMap.get(tableName);
                dbSaveOrNamePo.setTablejson(tablejson);
                dbSaveOrNamePo.setIsupdate(dbSaveoreditinfoTable.getIsUpdate());
                dbSaveOrNamePo.setId(dbSaveoreditinfoTable.getId());
            }
        }

        result.addAll(tableMap.values());

        return R.data(result);
    }

}




