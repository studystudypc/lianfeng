package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lianfeng.common.constants.LFanConstants;
import com.lianfeng.common.utils.JdbcUtil;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.po.ConformSQLPo;
import com.lianfeng.service.IDBTransmitConditionService;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.Condition;
import com.lianfeng.vo.ConditionVo;
import com.lianfeng.vo.ConditionsVO;
import com.lianfeng.vo.DBNameVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

import static com.lianfeng.constans.DBConstants.*;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2025-01-23 10:39
 */
@Service
public class DBTransmitConditionServiceImpl implements IDBTransmitConditionService {

    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;

    /**
     * 根据表查询数据库中字段
     * 1、根据conditionVo、listTable查询数据库内容
     * 2、把查询到的内容放在List的Map集合中 asset_id->5，date_depreciation->2033-12-31
     *
     * @param conditionVo 条件字段
     * @param listTable   表名
     * @return
     */
    @Override
    public List<Map<String, List<Map<String, String>>>> queryCondition(ConditionVo conditionVo, List<DBNameVo> listTable) {
        List<Map<String, List<Map<String, String>>>> result = new ArrayList<>();//返回的值
        List<String> sqlList = new ArrayList<>();//存放查询的sql
        // 构造查询条件
        StringBuilder conditionBuilder = new StringBuilder();
        String[] fields = conditionVo.getField();
        String[] values = conditionVo.getValue();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                conditionBuilder.append(" AND ");
            }
            conditionBuilder.append(fields[i]).append(" = ? ");
        }

        String baseSql = OPERTION_SELECT + OPERTION_SPACING + FILE_ALL + OPERTION_SPACING + OPERTION_FROM + OPERTION_SPACING + "%s" + OPERTION_SPACING + OPERTION_WHERE + OPERTION_SPACING + conditionBuilder.toString();
        for (DBNameVo dbNameVo : listTable) {
            String tableName = dbNameVo.getTableName();
            String sql = String.format(baseSql, tableName);
            sqlList.add(sql);

            // 执行查询并处理结果
            try {
                List<Map<String, String>> tableResult = executeQuery(sql, values);
                Map<String, List<Map<String, String>>> tableMap = new HashMap<>();
                tableMap.put(tableName, tableResult);
                result.add(tableMap);
            } catch (Exception e) {
                System.out.println(tableName + "表中不存在" + e.getMessage());
//                TODO 处理错误日志
                continue;
            }
        }

        return result;
    }

    /**
     * 查找出来符合条件的sql
     *
     * @param queryField
     * @return
     */
    @Override
    public ConformSQLPo conformSQL(List<Map<String, List<Map<String, String>>>> queryField) {
        ConformSQLPo conformSQLPo = new ConformSQLPo();
        List<Map<String, List<Map<String, String>>>> conformTables = new ArrayList<>();
        List<Map<String, List<Map<String, String>>>> nonConformTables = new ArrayList<>();

        for (Map<String, List<Map<String, String>>> tableData : queryField) {
            for (Map.Entry<String, List<Map<String, String>>> entry : tableData.entrySet()) {
                if (entry.getValue().size() > 0) {
                    Map<String, List<Map<String, String>>> tableMap = new HashMap<>();
                    tableMap.put(entry.getKey(), entry.getValue());
                    conformTables.add(tableMap);
                } else {
                    Map<String, List<Map<String, String>>> tableMap = new HashMap<>();
                    tableMap.put(entry.getKey(), entry.getValue());
                    nonConformTables.add(tableMap);
                }

            }
        }
        conformSQLPo.setConform(conformTables);
        conformSQLPo.setIsConform(nonConformTables);

        return conformSQLPo;
    }

    /**
     * 拼接sql语句
     *
     * @param queryField
     * @return
     */
    @Override
    public List<String> splicingSQL(List<Map<String, List<Map<String, String>>>> queryField) {
        List<String> sqlList = new ArrayList<>();
        String baseSql = "REPLACE INTO %s (%s) VALUES %s;";

        for (Map<String, List<Map<String, String>>> item : queryField) {
            for (Map.Entry<String, List<Map<String, String>>> entry : item.entrySet()) {
                String tableName = entry.getKey();//表名
                List<Map<String, String>> records = entry.getValue();//字段的集合

                if (records.size() > 0) {
                    Map<String, String> firstRecord = records.get(0);
                    List<String> fieldNames = new ArrayList<>(firstRecord.keySet());//字段名字
                    String fields = String.join(",", fieldNames);//字段的值拼接 示例：date_depreciation,accumulated_depreciation_code,company_id,datetime_create,accumulated_account_type,asset_id,log_index,is_init_value,des,asset_value_real,asset_code,asset_value,accumulated_depreciation_year,year_depreciation

                    StringBuilder valuesBuilder = new StringBuilder();
                    for (Map<String, String> record : records) {
                        List<String> values = new ArrayList<>();
                        for (String fieldName : fieldNames) {
                            String value = record.get(fieldName);//字段名字对应的字段值
                            if (value == null || value.isEmpty() || value.length() == 0) {
                                values.add("'" + "NULL" + "'");//null
                            } else {
                                values.add("'" + value.replace("'", "''") + "'");
                            }
                        }
                        String valueString = String.join(",", values);
                        valuesBuilder.append("(").append(valueString).append("),");
                    }
                    if (valuesBuilder.length() > 0) {
                        valuesBuilder.setLength(valuesBuilder.length() - 1);
                    }
                    String sql = String.format(baseSql, tableName, fields, valuesBuilder.toString());
                    sqlList.add(sql);
                }
            }
        }

        return sqlList;
    }

    /**
     * 执行sql
     *
     * @param sqls
     */
    @Override
    public void transmitSQL(List<String> sqls) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = targerConnection();

            for (String sql : sqls) {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.execute();
            }

        } catch (SQLException e) {

        } finally {
            JdbcUtil.release(connection, preparedStatement, resultSet);
        }
    }

    /**
     * 根据conditionsVO拼出查询的sql
     * 把查询的结果集放到一个集合中
     *
     * @param conditionsVO
     */
    @Override
    public List<Map<String, String>> queryConditions(ConditionsVO conditionsVO) {
        List<Condition> conditionList = conditionsVO.getCondition();//查询条件

        StringBuilder whereSQL = new StringBuilder();//where后面的语句
        for (int i = 0; i < conditionList.size(); i++) {
            if (i > 0) {
                whereSQL.append(" AND ");
            }
            Condition condition = conditionList.get(i);
            whereSQL.append("`" + condition.getField() + "`").append(condition.getOperator()).append("'" + condition.getValue() + "'");
        }
        String baseSQL = "SELECT * FROM " + conditionsVO.getTableName() + " WHERE " + whereSQL;

        return executesQuery(baseSQL);//执行查询，并把查询结果封装到结果集中
    }


    /**
     * 拼接sql语句
     *
     * @param selectSQl
     * @return
     */
    @Override
    public String splicingsSQL(List<Map<String, String>> selectSQl, ConditionsVO conditionsVO) {
        Map<String, String> firstRecord = selectSQl.get(0);
        List<String> fieldNames = new ArrayList<>(firstRecord.keySet());//把字段放到集合中
        String fields = String.join(",", fieldNames);//字段拼接
        String tableName = conditionsVO.getTableName();//表的名字
        //上述通过第一条数据获取字段值

        String baseSQL = "REPLACE INTO %s (%s) VALUES %s;";//拼接语句模板

        StringBuilder valuesBuilder = new StringBuilder();//存放value的值

        for (Map<String, String> record : selectSQl) {
            List<String> values = new ArrayList<>();
            for (String fieldName : fieldNames) {
                String value = record.get(fieldName);//字段名字对应的字段值
                if (value == null || value.isEmpty()) {
                    values.add("NULL");
                } else {
                    values.add("'" + value.replace("'", "''") + "'");
                }
            }
            String valueString = String.join(",", values);
            valuesBuilder.append("(").append(valueString).append("),");
        }
        if (valuesBuilder.length() > 0) {
            valuesBuilder.setLength(valuesBuilder.length() - 1);
        }
        String sql = String.format(baseSQL,tableName,fields,valuesBuilder);
        return sql;
    }




    /*----------------------private--------------------------*/

    /**
     * 执行sql
     *
     * @param sql
     */
    @Override
    public void transmitsSQL(String sql) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = targerConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtil.release(connection,preparedStatement,resultSet);
        }
    }

    /**
     * 执行查询，并把查询结果封装到结果集中
     *
     * @param baseSQL
     */
    private List<Map<String, String>> executesQuery(String baseSQL) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Map<String, String>> result = new ArrayList<>();//结果集
        try {
            connection = sourceConnection();
            preparedStatement = connection.prepareStatement(baseSQL);
            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    row.put(columnName, columnValue);
                }
                result.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.release(connection, preparedStatement, resultSet);
        }

        return result;
    }

    /**
     * 执行sql查询，如果sql执行报错，那就不执行
     *
     * @param sql
     * @param values
     * @return
     */
    private List<Map<String, String>> executeQuery(String sql, String[] values) {
        List<Map<String, String>> result = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = sourceConnection();// 获取数据库连接
            preparedStatement = connection.prepareStatement(sql);

            // 设置查询条件
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setString(i + 1, values[i]);
            }

            resultSet = preparedStatement.executeQuery();

            // 处理查询结果
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    row.put(columnName, columnValue);
                }
                result.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtil.release(connection, preparedStatement, resultSet);
        }
        return result;
    }

    /**
     * 源数据库连接
     *
     * @return
     */
    private Connection sourceConnection() {
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> dbConnectionInfoList = iDbConnectionInfoService.list(queryWrapper);
        DbConnectionInfo sourceInfo = dbConnectionInfoList.get(0); // 第一个数据源
        return JdbcUtil.getConnection(sourceInfo.getDbUrl(), sourceInfo.getDbUsername(), sourceInfo.getDbPassword());
    }

    /**
     * 目标数据库连接
     *
     * @return
     */
    private Connection targerConnection() {
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> dbConnectionInfoList = iDbConnectionInfoService.list(queryWrapper);
        DbConnectionInfo targerInfo = dbConnectionInfoList.get(1); // 第一个数据源
        return JdbcUtil.getConnection(targerInfo.getDbUrl(), targerInfo.getDbUsername(), targerInfo.getDbPassword());
    }


}
