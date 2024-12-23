package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.constants.LFanConstants;
import com.lianfeng.common.utils.JdbcUtil;
import com.lianfeng.mapper.DbConnectionInfoMapper;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.model.entity.ExampleTable;
import com.lianfeng.po.CompareTablePo;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DbConnectionInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lianfeng.common.constants.LFanConstants.ZERO_INT;

/**
* @author LCP
* @description 针对表【db_connection_info(数据库连接信息表)】的数据库操作Service实现
* @createDate 2024-12-21 09:54:57
*/
@Service
public class DbConnectionInfoServiceImpl extends ServiceImpl<DbConnectionInfoMapper, DbConnectionInfo>
    implements IDbConnectionInfoService {

    @Autowired
    private DbConnectionInfoMapper dbConnectionInfoMapper;

    //保存或更新数据库源信息
    @Override
    public void saveOrUpdateApartment(DbConnectionInfoVo dbConnectionInfoVo) {
        DbConnectionInfo dbConnectionInfo = new DbConnectionInfo();
        BeanUtils.copyProperties(dbConnectionInfoVo,dbConnectionInfo);
        super.saveOrUpdate(dbConnectionInfo);
    }

    /**
     * @Author liuchuaning
     * @Description
     * 对比表的结构
     * @Date 2024-12-21 12:11
     * @Param
     * @return CompareTablePo 返回对比表的数据
     **/
    @Override
    public CompareTablePo compareTable(String table) throws SQLException {
        // 查询数据库信息
        StringBuilder sql = new StringBuilder("DESC " + table);
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = list(queryWrapper);

        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        Connection sourceConnection = JdbcUtil.getConnection(
                sourceInfo.getDbUrl(),
                sourceInfo.getDbUsername(),
                sourceInfo.getDbPassword()
        ); // 连接数据源
        PreparedStatement sourceIPreparedStatement = sourceConnection.prepareStatement(sql.toString()); // 查询表结构
        ResultSet sourceResultSet = sourceIPreparedStatement.executeQuery();

        // 创建一个列表来存储表结构信息
        List<ExampleTable> sourceTables = new ArrayList<>();
        while (sourceResultSet.next()) {
            ExampleTable sourceTable = new ExampleTable(); // 为每一行创建一个新的ExampleTable对象

            // 存放查询"DESC " + table数据
            String field = sourceResultSet.getString("Field");   // 字段名
            String type = sourceResultSet.getString("Type");     // 字段类型
            String isNull = sourceResultSet.getString("Null");   // 是否允许为空
            String key = sourceResultSet.getString("Key");       // 主键信息
            String defaultValue = sourceResultSet.getString("Default"); // 默认值
            String extra = sourceResultSet.getString("Extra");   // 额外信息

            sourceTable.setDbField(field);
            sourceTable.setDbType(type);
            sourceTable.setDbNull(isNull);
            sourceTable.setDbKey(key);
            sourceTable.setDbDefault(defaultValue);
            sourceTable.setDbExtra(extra);

            sourceTables.add(sourceTable);
        }
        // 释放数据库资源
        JdbcUtil.release(sourceConnection, sourceIPreparedStatement, sourceResultSet);

        DbConnectionInfo targetInfo = list.get(1); // 第一个数据源
        Connection targetConnection = JdbcUtil.getConnection(
                targetInfo.getDbUrl(),
                targetInfo.getDbUsername(),
                targetInfo.getDbPassword()
        ); // 连接数据源
        PreparedStatement targetPreparedStatement = targetConnection.prepareStatement(sql.toString()); // 查询表结构
        ResultSet targetResultSet = targetPreparedStatement.executeQuery();

        //创建第二个列表存储数据结构信息
        List<ExampleTable> targetTables = new ArrayList<>();
        while (targetResultSet.next()) {
            ExampleTable targetTable = new ExampleTable(); // 为每一行创建一个新的ExampleTable对象

            // 存放查询"DESC " + table数据
            String field = targetResultSet.getString("Field");   // 字段名
            String type = targetResultSet.getString("Type");     // 字段类型
            String isNull = targetResultSet.getString("Null");   // 是否允许为空
            String key = targetResultSet.getString("Key");       // 主键信息
            String defaultValue = targetResultSet.getString("Default"); // 默认值
            String extra = targetResultSet.getString("Extra");   // 额外信息

            targetTable.setDbField(field);
            targetTable.setDbType(type);
            targetTable.setDbNull(isNull);
            targetTable.setDbKey(key);
            targetTable.setDbDefault(defaultValue);
            targetTable.setDbExtra(extra);

            targetTables.add(targetTable);
        }
        // 释放数据库资源
        JdbcUtil.release(targetConnection, targetPreparedStatement, targetResultSet);

        //比较元素是不是相同的
        boolean isEqual = sourceTables.equals(targetTables);
        boolean isSizeEqual = sourceTables.size() == targetTables.size();

        if(!isEqual && !isSizeEqual){
            System.out.println("数据表不一样");
            checkTable(sourceTables,targetTables);
        }





        return null;
    }

    //检查二个数据表什么结构不一样，返回的信息存入数据中
    private void checkTable(List<ExampleTable> sourceTables, List<ExampleTable> targetTables) {

    }


}




