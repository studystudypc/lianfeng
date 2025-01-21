package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.constants.LFanConstants;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.response.R;
import com.lianfeng.common.utils.JdbcUtil;
import com.lianfeng.common.utils.JsonUtiles;
import com.lianfeng.controller.WebSocket;
import com.lianfeng.mapper.DBTransmitMapper;
import com.lianfeng.mapper.DataTransferLogMapper;
import com.lianfeng.model.entity.DataTransferLog;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.po.DBTransmitPo;
import com.lianfeng.po.DataTransferLogPo;
import com.lianfeng.service.IDBTransmitService;
import com.lianfeng.service.IDataTransferLogService;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.service.RedisService;
import com.lianfeng.vo.DBTransmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.templateparser.markup.decoupled.IDecoupledTemplateLogicResolver;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lianfeng.common.response.ResponseCode.DATABASE_CONNECTION_INSUFFICIENT;
import static com.lianfeng.common.response.ResponseCode.PRIMARY_KEY_NOT_FOUND;
import static com.lianfeng.constans.RedisConstant.PROGRESS_BAR_ALL;
import static com.lianfeng.constans.WebSocketConstant.*;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-26 10:39
 */
@Service
public class DBTransmitServiceImpl extends ServiceImpl<DBTransmitMapper, Object> implements IDBTransmitService {

    @Autowired
    private IDbConnectionInfoService iDbConnectionInfoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private IDataTransferLogService iDataTransferLogService;


    /**
     * @param keyName
     * @return DBTransmitPo
     * @Author liuchuanping
     * @Description 全表传输
     * 1.连接数据库（源数据库和目标数据库）
     * 2.传输数据
     * @Date 2024-12-26 11:08
     * @Param tableName
     **/
    @Override
    public DBTransmitPo dBTransmit(String tableName, String[] keyName) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
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
//        REPLACE INTO dict (is_auto_match, dict_id, industry_id, is_from_public, customer_used, description, key_is_word, company_id_create, dict_order, vendor, disabled, state_id, is_approve, dict_type, time_create, company_id, user_id_update, user_id_approve, user_id_create, key_words, dict_status, is_public, account_code, beizhu, account, ask_manager) VALUES ('0', '6', '1', '0', '0', NULL, '0', '0', '0', 'Amazon', '0', '1', '0', '0', NULL, '0', '0', '0', '0', 'AMZN', '0', '0', NULL, NULL, 'Purchases', '0')


        targetTransmit(targetInfo.getDbUsername(),
                targetInfo.getDbPassword(),
                targetInfo.getDbUrl(),
                updateSql,
                tableName,
                keyName);


        return dbTransmitPo;
    }

    /**
     * 需要更新的字段
     *
     * @param tableName
     * @param keyName
     * @param fieldName
     * @return
     * @throws SQLException
     */
    @Override
    public DBTransmitPo dBTransmit(String tableName, String[] keyName, String[] fieldName) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }
        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        DbConnectionInfo targetInfo = list.get(1); // 第二个数据源


        List<String> updateSql = fieldNameConnection(sourceInfo.getDbUsername(),
                sourceInfo.getDbPassword(),
                sourceInfo.getDbUrl(),
                keyName,
                tableName,
                fieldName);//查询源数据，拼接更新sql语句。
//        INSERT INTO dict(dict_id, state_id, company_id, vendor) VALUES ('2', '1', '0', '88 Marketplace') ON DUPLICATE KEY UPDATE company_id='0', vendor='88 Marketplace'

        fieldNamdBTransmit(targetInfo.getDbUsername(),
                targetInfo.getDbPassword(),
                targetInfo.getDbUrl(),
                updateSql,
                tableName,
                keyName);

        return dbTransmitPo;
    }

    /**
     * 需要更新的主键
     *
     * @param tableName
     * @param keyName
     * @param keyValue
     * @return
     */
    @Override
    public DBTransmitPo dBTransmitKey(String tableName, String[] keyName, String[] keyValue) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }
        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        DbConnectionInfo targetInfo = list.get(1); // 第二个数据源

        List<String> updateSql = keyValueConnection(sourceInfo.getDbUsername(),
                sourceInfo.getDbPassword(),
                sourceInfo.getDbUrl(),
                keyName,
                tableName,
                keyValue);//查询源数据，拼接更新sql语句。
//        INSERT INTO dict (dict_id, state_id, company_id)
//        VALUES ('5039', '8888', '9999')
//        ON DUPLICATE KEY UPDATE
//        state_id = '8888',
//                company_id = '9999';

        redisService.setValue(PROGRESS_BAR_ALL + tableName, String.valueOf(updateSql.size()));
        keyValueBTransmit(targetInfo.getDbUsername(),
                targetInfo.getDbPassword(),
                targetInfo.getDbUrl(),
                updateSql,
                tableName,
                keyValue,
                keyName);

        return dbTransmitPo;
    }

    /**
     * 全字段更新
     *
     * @param tableName
     * @param keyName
     * @param keyValue
     * @param fieldName
     * @return
     * @throws SQLException
     */
    @Override
    public DBTransmitPo dBTransmit(String tableName, String[] keyName, String[] keyValue, String[] fieldName) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }
        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        DbConnectionInfo targetInfo = list.get(1); // 第二个数据源

        StringBuilder sqlSelect = new StringBuilder("SELECT ");//查询多主键的sql
        for (int i = 0; i < fieldName.length; i++) {
            if (i > 0) {
                sqlSelect.append(",");
            }
            sqlSelect.append(fieldName[i]);
        }
        sqlSelect.append(" FROM " + tableName);
        sqlSelect.append(" WHERE ");
        for (int i = 0; i < keyValue.length; i++) {
            if (i > 0) {
                sqlSelect.append(" AND ");
            }
            sqlSelect.append(keyName[i]).append(" = '").append(keyValue[i].replace("'", "''")).append("'");
        }

        List<String> updateSql = updateConnection(sourceInfo.getDbUsername(),
                sourceInfo.getDbPassword(),
                sourceInfo.getDbUrl(),
                keyName,
                keyValue,
                fieldName,
                tableName,
                sqlSelect.toString());//查询源数据，拼接更新sql语句。
        redisService.setValue(PROGRESS_BAR_ALL + tableName, String.valueOf(updateSql.size()));
//INSERT INTO dict(dict_id,state_id,dict_order) VALUES ( '10' , '1' , '0') ON DUPLICATE KEY UPDATE dict_order = '0'
        allBTransmit(targetInfo.getDbUsername(),
                targetInfo.getDbPassword(),
                targetInfo.getDbUrl(),
                updateSql,
                tableName,
                keyName);

        return dbTransmitPo;
    }

    /**
     * 返回某个表的字段名字
     *
     * @param name
     * @return
     */
    @Override
    public DBTransmitPo reField(String name) throws SQLException {
        DBTransmitPo dbTransmitPo = new DBTransmitPo();

        String sql = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '" + name + "'";
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = iDbConnectionInfoService.list(queryWrapper);//数据源信息
        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }
        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源

        Connection connection = JdbcUtil.getConnection(sourceInfo.getDbUrl(), sourceInfo.getDbUsername(), sourceInfo.getDbPassword()); // 连接信息
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        List<String> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(resultSet.getString("COLUMN_NAME"));
        }
        dbTransmitPo.setFieldName(result);

        JdbcUtil.release(connection, preparedStatement, resultSet);
        return dbTransmitPo;
    }

    /****************************************************private*****************************************/

    /**
     * 全字段更新sql执行
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param updateSql
     * @throws SQLException
     */
    private void allBTransmit(String dbUsername, String dbPassword, String dbUrl, List<String> updateSql, String tableName,String[] keyName) throws SQLException {
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = null;
        int count = 0;

        for (String sql : updateSql) {
            preparedStatement = connection.prepareStatement(sql);
            DataTransferLog dataTransferLog = new DataTransferLog();
            dataTransferLog.setTransferStatus(0);
            dataTransferLog.setStartTime(new Date());
            dataTransferLog.setTableName(tableName);
            boolean execute = preparedStatement.execute();
            String field = JsonUtiles.objectToJson(allBTransmitToJson(sql, keyName));

            if (!execute){
                dataTransferLog.setField(field);
                dataTransferLog.setTransferStatus(1);
            }else {
                dataTransferLog.setTransferStatus(0);
                dataTransferLog.setErrorMessage(field);
            }
            iDataTransferLogService.save(dataTransferLog);

//            ResultSetMetaData metaData = resultSet.getMetaData();
            count++;
            Object value = redisService.getValue(PROGRESS_BAR_ALL + tableName);
            Integer allCount = Integer.valueOf(String.valueOf(value));
            int i = (int) ((count * 100.0) / allCount);
            webSocket.sendOneMessage(ALL_TABLE + tableName, String.valueOf(i));
        }
        connection.close();
        preparedStatement.close();
        redisService.deleteValue(PROGRESS_BAR_ALL);
    }

    private String allBTransmitToJson(String sql, String[] keyName) {

        String columnsRegex = "\\(([^\\)]+)\\)";
        Pattern pattern = Pattern.compile(columnsRegex);
        Matcher matcher = pattern.matcher(sql);
        String columns = "";
        String values = "";
        if (matcher.find()) {
            columns = matcher.group(1);
        }

        sql.lastIndexOf("VALUES");
        sql.lastIndexOf(")");
        String substring = sql.substring(sql.lastIndexOf("VALUES") + 8, sql.lastIndexOf(")"));

        values = substring;

        String[] splitColumns = columns.split(",");
        String[] splitValues = values.split(",");

        Map<String, String> columnValueMap = new HashMap<>();

        for (int i = 0; i < splitColumns.length; i++) {
            columnValueMap.put(splitColumns[i], splitValues[i]);
        }
        StringBuilder result = new StringBuilder();
        for (String key : keyName) {
            String value = columnValueMap.get(key);
            result.append(key+":"+value).append(",");

        }
        return result.delete(result.lastIndexOf(","),result.length()).toString();
    }

    /**
     * 全字段更新sql
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param keyName
     * @param keyValue
     * @param fieldName
     * @param tableName
     * @param sqlSelect
     * @return
     * @throws SQLException
     */
    private List<String> updateConnection(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String[] keyValue, String[] fieldName, String tableName, String sqlSelect) throws SQLException {

        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);
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

        if (rows.size() == 0) {
            throw new LFBusinessException(PRIMARY_KEY_NOT_FOUND.getCode(), DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }

        List<String> listSql = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + "(");
            List<String> columns = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            for (String columnName : row.keySet()) {
                columns.add(columnName);
            }
            for (Object value : row.values()) {
                values.add(value);
            }
            for (int i = 0; i < keyName.length; i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append(keyName[i]);
            }
            for (int i = 0; i < fieldName.length; i++) {
                if (i >= 0) {
                    sql.append(",");
                }
                sql.append(fieldName[i]);
            }
            sql.append(") VALUES ( ");
            for (int i = 0; i < keyValue.length; i++) {
                if (i > 0) {
                    sql.append(" , ");
                }
                sql.append("'").append(keyValue[i]).append("'");
            }
            for (int i = 0; i < columns.size(); i++) {
                if (i >= 0) {
                    sql.append(" , ");
                }
                sql.append("'").append(values.get(i)).append("'");
            }
            sql.append(") ON DUPLICATE KEY UPDATE ");

            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(columns.get(i)).append(" = '").append(values.get(i)).append("'");
            }


            listSql.add(sql.toString());
        }

        JdbcUtil.release(connection, preparedStatement, resultSet);

        return listSql;
    }

    /**
     * 需要更新的主键sql执行
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param updateSql
     * @throws SQLException
     */
    private void keyValueBTransmit(String dbUsername, String dbPassword, String dbUrl, List<String> updateSql, String tableName,String[] keyName, String[] keyValue) throws SQLException {
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = null;
        int count = 0;

        for (String sql : updateSql) {
            preparedStatement = connection.prepareStatement(sql);
            DataTransferLog dataTransferLog = new DataTransferLog();
            dataTransferLog.setTransferStatus(0);
            dataTransferLog.setStartTime(new Date());
            dataTransferLog.setTableName(tableName);
            boolean execute = preparedStatement.execute();

            String field = JsonUtiles.objectToJson(fieldToJon(sql, keyName,keyValue));
            if (!execute){
                dataTransferLog.setField(field);
                dataTransferLog.setTransferStatus(1);
            }else {
                dataTransferLog.setTransferStatus(0);
                dataTransferLog.setErrorMessage(field);
            }
            iDataTransferLogService.save(dataTransferLog);

//            ResultSetMetaData metaData = resultSet.getMetaData();
            count++;
            Object value = redisService.getValue(PROGRESS_BAR_ALL + tableName);
            Integer allCount = Integer.valueOf(String.valueOf(value));
            int i = (int) ((count * 100.0) / allCount); // 计算百分比
            webSocket.sendOneMessage(ALL_TABLE + tableName, String.valueOf(i));

        }
        connection.close();
        preparedStatement.close();
        redisService.deleteValue(PROGRESS_BAR_ALL);
    }

    /**
     * 需要更新的主键sql拼接
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param keyName
     * @param tableName
     * @param keyValue
     * @return
     * @throws SQLException
     */
    private List<String> keyValueConnection(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String tableName, String[] keyValue) throws SQLException {
        // 全表查询
        StringBuilder whereClause = new StringBuilder(" WHERE ");
        for (int i = 0; i < keyName.length; i++) {
            String escapedValue = keyValue[i].replace("'", "''"); // 简单的转义，将单引号替换为两个单引号
            whereClause.append(keyName[i]).append(" = '").append(escapedValue).append("' ");
            if (i < keyName.length - 1) {
                whereClause.append(" AND ");
            }
        }
        String selectSql = "SELECT * FROM " + tableName + whereClause.toString();
        //SELECT * FROM dict WHERE dict_id = 5034,state_id = 50;
        //keyName = [dict_id,state_id]
        //value = [5034,50]

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

        List<String> listSql = new ArrayList<>();//sql语句
        Set<String> keyNameSet = new HashSet<>(Arrays.asList(keyName));//主键字段

        for (Map<String, Object> row : rows) {
            List<String> keyColumns = Arrays.asList(keyName);
            List<String> keyValues = Arrays.asList(keyValue);

            List<String> fieldColumns = new ArrayList<>();
            List<String> fieldValues = new ArrayList<>();

            for (String columnName : row.keySet()) {
                if (!keyNameSet.contains(columnName)) {
                    fieldColumns.add(columnName);
                    Object columnValue = row.get(columnName);
                    // 检查是否为字符串 "NULL"，如果是，则使用 NULL 关键字
                    String valueToUse = (columnValue != null && columnValue.toString().equals("NULL")) ? "NULL" : columnValue == null ? "NULL" : "'" + columnValue.toString().replace("'", "''") + "'";
                    fieldValues.add(valueToUse);
                }
            }

            StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder valuesSql = new StringBuilder("VALUES (");
            StringBuilder updateSql = new StringBuilder("ON DUPLICATE KEY UPDATE ");

            for (int i = 0; i < keyColumns.size(); i++) {
                if (i > 0) {
                    insertSql.append(", ");
                    valuesSql.append(", ");
                }
                insertSql.append(keyColumns.get(i));
                valuesSql.append(keyValues.get(i));
            }

            for (int i = 0; i < fieldColumns.size(); i++) {
                if (i > 0 || !keyColumns.isEmpty()) {
                    insertSql.append(", ");
                    valuesSql.append(", ");
                }
                insertSql.append(fieldColumns.get(i));
                valuesSql.append(fieldValues.get(i));
            }

            insertSql.append(") ").append(valuesSql).append(")");
            for (int i = 0; i < fieldColumns.size(); i++) {
                if (i > 0) {
                    updateSql.append(", ");
                }
                updateSql.append(fieldColumns.get(i)).append(" = ").append(fieldValues.get(i));
            }

            String finalSql = insertSql.toString() + " " + updateSql.toString();
            listSql.add(finalSql);
        }

        JdbcUtil.release(connection, preparedStatement, resultSet);

        return listSql;
    }

    /**
     * 需要更新的字段的sql执行
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param updateSql
     */
    private void fieldNamdBTransmit(String dbUsername, String dbPassword, String dbUrl, List<String> updateSql, String tableName,String[] keyName) throws SQLException {
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = null;
        int count = 0;


        for (String sql : updateSql) {
            preparedStatement = connection.prepareStatement(sql);
            DataTransferLog dataTransferLog = new DataTransferLog();
            dataTransferLog.setTransferStatus(0);
            dataTransferLog.setStartTime(new Date());
            dataTransferLog.setTableName(tableName);
            boolean execute = preparedStatement.execute();

            String field = JsonUtiles.objectToJson(fieldToJson(sql, keyName));

            if (!execute){
                dataTransferLog.setField(field);
                dataTransferLog.setTransferStatus(1);
            }else {
                dataTransferLog.setTransferStatus(0);
                dataTransferLog.setErrorMessage(field);
            }
            iDataTransferLogService.save(dataTransferLog);


//            ResultSetMetaData metaData = resultSet.getMetaData();
            count++;
            Object value = redisService.getValue(PROGRESS_BAR_ALL + tableName);
            Integer allCount = Integer.valueOf(String.valueOf(value));
            int i = (int) ((count * 100.00) / allCount);
            webSocket.sendOneMessage(ALL_TABLE + tableName, String.valueOf(i));

        }
        connection.close();
        preparedStatement.close();
        redisService.deleteValue(PROGRESS_BAR_ALL + tableName);
    }

    private String fieldToJson(String sql, String[] keyName) {
//        INSERT INTO dict(dict_id, state_id, company_id, vendor) VALUES ('2', '1', '0', '88 Marketplace') ON DUPLICATE KEY UPDATE company_id='0', vendor='88 Marketplace'
        String columnsRegex = "\\(([^\\)]+)\\)";
        Pattern pattern = Pattern.compile(columnsRegex);
        Matcher matcher = pattern.matcher(sql);

        String columns = "";
        String values = "";

        if (matcher.find()) {
            columns = matcher.group(1);
        }
        int index = sql.lastIndexOf(")");
        String substring = sql.substring(sql.lastIndexOf("VALUES") + 8, index);
        values = substring;

        String[] columnsArray = columns.split(", ");
        String[] valuesArray = values.split(", ");

        Map<String, String> columnValueMap = new HashMap<>();
        try {
            for (int i = 0; i < columnsArray.length; i++) {
                columnValueMap.put(columnsArray[i],valuesArray[i]);
            }
        }catch (RuntimeException e){
            e.printStackTrace();

        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < keyName.length; i++) {
            String s = columnValueMap.get(keyName[i]);
            result.append(keyName[i]+":"+s).append(",");
        }
        return result.delete(result.lastIndexOf(","),result.length()).toString();
    }

    /**
     * 需要更新字段的sql
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param keyName
     * @param tableName
     * @param fieldName
     * @return
     */
    private List<String> fieldNameConnection(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String tableName, String[] fieldName) throws SQLException {
        // 多主键字段和字段
        String[] strings = new String[keyName.length + fieldName.length];
        for (int i = 0; i < keyName.length; i++) {
            strings[i] = keyName[i];
        }
        for (int i = 0; i < fieldName.length; i++) {
            strings[i + keyName.length] = fieldName[i];
        }
        // 查询需要的字段
        StringBuilder selectSql = new StringBuilder("SELECT ");
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                selectSql.append(", ");
            }
            selectSql.append(strings[i]);
        }
        selectSql.append(" FROM ").append(tableName);

        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword);
        PreparedStatement preparedStatement = connection.prepareStatement(selectSql.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        List<Map<String, Object>> rows = new ArrayList<>(); // 存放每行数据

        // 空值也存入
        if (!resultSet.next()) {
            rows.add(new HashMap<>());
        } else {
            // 如果有数据，继续处理
            do {
                HashMap<String, Object> row = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) { // 修正列索引从 1 开始
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    row.put(columnName, columnValue);
                }
                rows.add(row);
            } while (resultSet.next());
        }

        List<String> listSql = new ArrayList<>();// 存放每条sql
        for (Map<String, Object> row : rows) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + "(");
            List<String> columns = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (String columnName : strings) {
                if (columns.size() > 0) {
                    sql.append(", ");
                }
                sql.append(columnName);
                columns.add(columnName);
            }
            sql.append(") VALUES (");

            for (String columnName : columns) {
                Object columnValue = row.get(columnName);
                String valueClause = columnValue == null ? "NULL" : "'" + columnValue.toString().replace("'", "''") + "'";
                values.add(valueClause);
                if (values.size() > 1) {
                    sql.append(", ");
                }
                sql.append(valueClause);
            }
            sql.append(") ON DUPLICATE KEY UPDATE ");

            List<String> updateClauses = new ArrayList<>();// 后面的值
            for (String columnName : strings) {
                if (Arrays.asList(keyName).contains(columnName)) {
                    continue;
                }
                Object columnValue = row.get(columnName);
                String valueClause = columnValue == null ? "NULL" : "'" + columnValue.toString().replace("'", "''") + "'";
                updateClauses.add(columnName + "=" + valueClause);
            }
            sql.append(String.join(", ", updateClauses));
            listSql.add(sql.toString());
        }
        JdbcUtil.release(connection, preparedStatement, resultSet);
        redisService.setValue(PROGRESS_BAR_ALL + tableName, String.valueOf(listSql.size()));//总条数，显示进度条用的
        return listSql;
    }

    /**
     * 全表传输的sql执行
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param updateSql
     * @throws SQLException
     */
    private void targetTransmit(String dbUsername, String dbPassword, String dbUrl, List<String> updateSql, String tableName, String[] keyName) throws SQLException {
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息
        PreparedStatement preparedStatement = null;
        int count = 0;

        for (String sql : updateSql) {
            preparedStatement = connection.prepareStatement(sql);
            DataTransferLog dataTransferLog = new DataTransferLog();
            dataTransferLog.setTransferStatus(0);
            dataTransferLog.setStartTime(new Date());
            dataTransferLog.setTableName(tableName);
            boolean execute = preparedStatement.execute();

            String field = JsonUtiles.objectToJson(fieldToJon(sql, keyName));

            if (!execute){
                dataTransferLog.setField(field);
                dataTransferLog.setTransferStatus(1);
            }else {
                dataTransferLog.setTransferStatus(0);
                dataTransferLog.setErrorMessage(field);
            }
            iDataTransferLogService.save(dataTransferLog);

            count++;
//            ResultSetMetaData metaData = resultSet.getMetaData();
            Object value = redisService.getValue(PROGRESS_BAR_ALL + tableName);
            Integer allCount = Integer.valueOf((String) value);
            int i = (int) ((count * 100.0) / allCount); // 计算百分比
            webSocket.sendOneMessage(ALL_TABLE + tableName, String.valueOf(i));
        }
        connection.close();
        preparedStatement.close();
        redisService.deleteValue(PROGRESS_BAR_ALL + tableName);
    }



    /**
     * 全表传输的sql
     *
     * @param dbUsername
     * @param dbPassword
     * @param dbUrl
     * @param keyName
     * @param tableName
     * @return
     * @throws SQLException
     */
    private List<String> updateConnection(String dbUsername, String dbPassword, String dbUrl, String[] keyName, String tableName) throws SQLException {
        // 全表查询
        String countSql = "SELECT COUNT(*) FROM " + tableName;//查询sql的条数，显示进度条用的

        String selectSql = "SELECT * FROM " + tableName;
        Connection connection = JdbcUtil.getConnection(dbUrl, dbUsername, dbPassword); // 连接信息

        PreparedStatement countStatement = connection.prepareStatement(countSql);
        ResultSet countResultSet = countStatement.executeQuery();
        int rowCount = 0;//数据库的条数
        if (countResultSet.next()) {
            rowCount = countResultSet.getInt(1);
        }
        countResultSet.close();
        countStatement.close();
        redisService.setValue(PROGRESS_BAR_ALL + tableName, String.valueOf(rowCount));

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

        JdbcUtil.release(connection, preparedStatement, resultSet); // 关闭连接

        return listSql;
    }

    private String fieldToJon(String sql,String[] keyName) {
        String columnsRegex = "\\(([^\\)]+)\\)";
        Pattern pattern = Pattern.compile(columnsRegex);
        Matcher matcher = pattern.matcher(sql);

        String columns = "";
        String values = "";

        if (matcher.find()) {
            columns = matcher.group(1);
        }
        values = sql.substring(sql.lastIndexOf("VALUES") + 8, sql.length() - 1);

        String[] columnsArray = columns.split(", ");
        String[] valuesArray = values.split(", ");

        Map<String, String> columnValueMap = new HashMap<>();
        try {
            for (int i = 0; i < columnsArray.length; i++) {
                columnValueMap.put(columnsArray[i],valuesArray[i]);
            }
        }catch (RuntimeException e){
            e.printStackTrace();

        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < keyName.length; i++) {
            String s = columnValueMap.get(keyName[i]);
            result.append(keyName[i]+":"+s).append(",");
        }
        return result.delete(result.lastIndexOf(","),result.length()).toString();
    }

    private String fieldToJon(String sql,String[] keyName,String[] keyValue) {
        Map<String, String> columnValueMap = new HashMap<>();
        for (int i = 0; i < keyName.length; i++) {
            columnValueMap.put(keyValue[i],keyName[i]);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < keyValue.length; i++) {
            String s = columnValueMap.get(keyValue[i]);
            result.append(keyValue[i]+":"+s).append(",");
        }
        return result.delete(result.lastIndexOf(","),result.length()).toString();

    }
}
