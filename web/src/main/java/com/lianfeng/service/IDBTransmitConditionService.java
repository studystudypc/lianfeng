package com.lianfeng.service;

import com.lianfeng.common.response.R;
import com.lianfeng.po.ConformSQLPo;
import com.lianfeng.vo.ConditionVo;
import com.lianfeng.vo.DBNameVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-23 9:49
 */

public interface IDBTransmitConditionService {

    /**
     * 根据表查询数据库中字段特定字段
     * @param conditionVo
     * @param listTable
     * @return
     */
    List<Map<String,List<Map<String, String>>>> queryCondition(ConditionVo conditionVo, List<DBNameVo> listTable);

    /**
     * 查找出来符合条件的sql
     * @param queryField
     * @return
     */
    ConformSQLPo conformSQL(List<Map<String, List<Map<String, String>>>> queryField);

    /**
     * 拼接sql
     * @param queryField
     * @return
     */
    List<String> splicingSQL(List<Map<String, List<Map<String, String>>>> queryField);

    void transmitSQL(List<String> sqls);
}
