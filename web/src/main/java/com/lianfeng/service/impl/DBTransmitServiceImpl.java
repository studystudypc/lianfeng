package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.constants.LFanConstants;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.utils.JdbcUtil;
import com.lianfeng.mapper.DBTransmitMapper;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.service.IDBTransmitService;
import com.lianfeng.service.IDbConnectionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-26 10:39
 */
@Service
public class DBTransmitServiceImpl extends ServiceImpl<DBTransmitMapper,Object> implements IDBTransmitService {

    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;

    /**
     * @Author liuchuanping
     * @Description 全表传输
     * 1.连接数据库（源数据库和目标数据库）
     * 2.传输数据
     * @Date 2024-12-26 11:08
     * @Param tableName
     * @param keyName
     * @return DBTransmitPo
     **/
    @Override
    public DBTransmitPo dBTransmit(String tableName, String[] keyName) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException("数据库连接信息不足");
        }
        StringBuilder sqlSelect = new StringBuilder("SELECT ");//查询多主键的sql
        for (int i = 0; i < keyName.length; i++) {
            if (i > 0) {
                sqlSelect.append(", ");
            }
            sqlSelect.append(keyName[i]);
        }
        sqlSelect.append(" FROM ").append(tableName);

        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        DbConnectionInfo targetInfo = list.get(1); // 第二个数据源


        List<String> updateSql = updateConnection(sourceInfo.getDbUsername(),
                sourceInfo.getDbPassword(),
                sourceInfo.getDbUrl(),
                keyName,
                tableName);//查询源数据，拼接更新sql语句。
        //UPDATE account SET trial_blance_id = '0', assistant_use1 = NULL, customer_use = '0', assistant_use2 = '0', account_id = '11', assistant_use = '1', account_name = '1', account_class = NULL, disabled = '0', calculate = '1', drake = NULL, demo1 = NULL WHERE account_des = NULL AND is_asset = '0' AND account_code = '11'

        targetTransmit(targetInfo.getDbUsername(),
                targetInfo.getDbPassword(),
                targetInfo.getDbUrl(),
                keyName,
                tableName,
                updateSql);



        return dbTransmitPo;
    }

    private void targetTransmit(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String tableName, List<String> updateSql) throws SQLException {
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = null;

        for (String sql : updateSql) {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
//            ResultSetMetaData metaData = resultSet.getMetaData();
        }
        connection.close();
        preparedStatement.close();
    }
    private List<String> updateConnection(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String tableName) throws SQLException {
        // 全表查询
        String selectSql = "SELECT * FROM " + tableName;
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = connection.prepareStatement(selectSql);
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        List<Map<String, Object>> rows = new ArrayList<>();// 存放每行数据

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                row.put(columnName, columnValue);
            }
            rows.add(row);
        }

        List<String> listSql = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            StringBuilder replaceSql = new StringBuilder("REPLACE INTO " + tableName + " (");
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (String columnName : row.keySet()) {
                Object columnValue = row.get(columnName);
                columns.add(columnName);
                String valueClause = columnValue == null ? "NULL" : "'" + columnValue.toString().replace("'", "''") + "'";
                values.add(valueClause);
            }

            // 拼接列名和值
            replaceSql.append(String.join(", ", columns)).append(") VALUES (").append(String.join(", ", values)).append(")");

            listSql.add(replaceSql.toString());
        }

        JdbcUtil.getConnection(dbUrl,dbUsername,dbPassword); // 关闭连接

        return listSql;
    }
}
