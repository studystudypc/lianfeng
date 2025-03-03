package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.constants.LFanConstants;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.utils.JdbcUtil;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.mapper.DbConnectionInfoMapper;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.model.entity.ExampleTable;
import com.lianfeng.po.CompareTablePo;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DBNameVo;
import com.lianfeng.vo.DbConnectionInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.lianfeng.common.response.ResponseCode.*;


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
        BeanUtils.copyProperties(dbConnectionInfoVo, dbConnectionInfo);
        super.saveOrUpdate(dbConnectionInfo);
    }

    /**
     * @return CompareTablePo 返回对比表的数据
     * @Author liuchuaning
     * @Description 对比表的结构
     * @Date 2024-12-21 12:11
     * @Param
     **/
    @Override
    public CompareTablePo compareTable(String table) throws SQLException {
        CompareTablePo compareTablePo = new CompareTablePo();

        // 查询数据库信息
        StringBuilder sql = new StringBuilder("DESC " + table);
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> list = list(queryWrapper);

        if (list.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT.getCode(),DATABASE_CONNECTION_INSUFFICIENT.getDesc());
        }

        DbConnectionInfo sourceInfo = list.get(0); // 第一个数据源
        DbConnectionInfo targetInfo = list.get(1); // 第二个数据源

        List<ExampleTable> sourceTables = queryTableStructure(sourceInfo, sql.toString(),table);
        List<ExampleTable> targetTables = queryTableStructure(targetInfo, sql.toString(),table);

        // 比较元素是不是相同的
        boolean isEqual = sourceTables.equals(targetTables);
        boolean isSizeEqual = sourceTables.size() == targetTables.size();

        if (!isEqual || !isSizeEqual) {
            List<String> differences = checkTable(sourceTables, targetTables, table);
            compareTablePo.setDifferencesList(differences);
        }
        compareTablePo.setTableName(table);
        return compareTablePo;
    }

    /**
     * 返回数据库的表名
     * @return
     */
    @Override
    public List<DBNameVo> returnTableName() throws SQLException {
        List<DBNameVo> list = new ArrayList<>();
        LambdaQueryWrapper<DbConnectionInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DbConnectionInfo::getIsDeleted, LFanConstants.ZERO_INT); // 0未删除
        List<DbConnectionInfo> dbConnectionInfoList = list(queryWrapper);

        if (dbConnectionInfoList.size() < 2) {
            throw new LFBusinessException(DATABASE_CONNECTION_INSUFFICIENT);
        }
        DbConnectionInfo sourceInfo = dbConnectionInfoList.get(0); // 第一个数据源
        Connection connection = JdbcUtil.getConnection(sourceInfo.getDbUrl(), sourceInfo.getDbUsername(), sourceInfo.getDbPassword());
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});
        while (resultSet.next()){
            DBNameVo dbNameVo = new DBNameVo();
            String table = resultSet.getString("TABLE_NAME");
            dbNameVo.setTableName(table);
            list.add(dbNameVo);
        }

        return list;
    }

    /**********************************private**********************************/
    private List<ExampleTable> queryTableStructure(DbConnectionInfo dbInfo, String sql,String tableName) throws SQLException {
        List<ExampleTable> tables = new ArrayList<>();
        try (Connection connection = JdbcUtil.getConnection(dbInfo.getDbUrl(), dbInfo.getDbUsername(), dbInfo.getDbPassword());
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ExampleTable table = new ExampleTable();
                table.setDbField(resultSet.getString("Field"));
                table.setDbType(resultSet.getString("Type"));
                table.setDbNull(resultSet.getString("Null"));
                table.setDbKey(resultSet.getString("Key"));
                table.setDbDefault(resultSet.getString("Default"));
                table.setDbExtra(resultSet.getString("Extra"));
                table.setTableName(tableName); // 注意这里的变量名应该是table，而不是table.getName()
                tables.add(table);
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("42S02")) { // 42S02是MySQL表不存在的状态码
                throw new LFBusinessException(DATABASE_TABLE_NOT_EXIST.getCode(),tableName + DATABASE_TABLE_NOT_EXIST.getDesc());
            } else {
                throw new LFBusinessException(QUERY_TABLE_STRUCTURE_ERROR.getCode(),QUERY_TABLE_STRUCTURE_ERROR.getDesc());
            }
        }
        return tables;
    }

    //检查二个数据表什么结构不一样，返回的信息存入数据中
    private List<String> checkTable(List<ExampleTable> sourceTables, List<ExampleTable> targetTables, String table) {
        List<String> differences = new ArrayList<>(); // 存放不同的数据

        if (sourceTables.size() != targetTables.size()) {
            differences.add("源表和目标表的字段数量不一致!");
        }

        for (int i = 0; i < sourceTables.size(); i++) {
            ExampleTable sourceTable = sourceTables.get(i);
            ExampleTable targetTable = targetTables.get(i);

            differences.addAll(compareTableDetails(sourceTable, targetTable));
        }

        // 记录差异
        if (!differences.isEmpty()) {
            try {
                // 创建日志文件
                String logFileName = table + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".txt";
                String logFilePath = DictConstants.LOG_PATH + "/" + logFileName;
                File logFile = new File(logFilePath);

                // 确保日志文件的目录存在
                File logDir = logFile.getParentFile();
                if (!logDir.exists() && !logDir.mkdirs()) {
                    throw new LFBusinessException(LOG_FILE_DIRECTORY_CREATION_FAILED.getCode(),LOG_FILE_DIRECTORY_CREATION_FAILED.getDesc());
                }

                // 写入差异信息到日志文件
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile))) {
                    writer.write(new Date() + "差异信息，表名 '" + table + "':");
                    writer.newLine();
                    for (String difference : differences) {
                        writer.write(difference);
                        writer.newLine();
                    }
                }
                System.out.println("差异信息已记录到日志文件：" + logFile.getAbsolutePath());
            } catch (IOException e) {
                throw new LFBusinessException("写入日志文件时发生错误：" + e.getMessage());
            }
        }

        return differences;
    }

    private List<String> compareTableDetails(ExampleTable sourceTable, ExampleTable targetTable) {
        //ExampleTable(id=null, tableName=account, dbField=account_id, dbType=int, dbNull=NO, dbKey=PRI, dbDefault=null, dbExtra=auto_increment, remarks=null, isDeleted=null, createdAt=null, updatedAt=null)
        //ExampleTable(id=null, tableName=account, dbField=account_id, dbType=int, dbNull=NO, dbKey=PRI, dbDefault=null, dbExtra=auto_increment, remarks=null, isDeleted=null, createdAt=null, updatedAt=null)

        List<String> differences = new ArrayList<>();

        // 数据库表名
        if (!sourceTable.getTableName().equals(targetTable.getTableName())) {
            differences.add(sourceTable.getTableName() + "源数据库表名和目标数据库表名不一样");
        }

        // 比较字段名称
        if (!sourceTable.getDbField().equals(targetTable.getDbField())) {
            differences.add(sourceTable.getDbField() + "字段名称不一样" +  ": source = " + sourceTable.getDbField() + ", target = " + targetTable.getDbField());
        }

        // 比较字段类型
        if (!sourceTable.getDbType().equals(targetTable.getDbType())) {
            differences.add(sourceTable.getDbField() + "字段类型" + ": source = " + sourceTable.getDbType() + ", target = " + targetTable.getDbType());
        }

        // 比较是否为空
        if (!sourceTable.getDbNull().equals(targetTable.getDbNull())) {
            differences.add(sourceTable.getDbField() + "字段空值" + ": source = " + sourceTable.getDbNull() + ", target = " + targetTable.getDbNull());
        }

        // 比较主键信息
        if (!sourceTable.getDbKey().equals(targetTable.getDbKey())) {
            differences.add(sourceTable.getDbField() + "字段主键" + ": source = " + sourceTable.getDbKey() + ", target = " + targetTable.getDbKey());
        }

        // 比较字段默认值
        if (!Objects.equals(sourceTable.getDbDefault(), targetTable.getDbDefault())) {
            differences.add(sourceTable.getDbField() + "字段默认值: source = " + sourceTable.getDbDefault() + ", target = " + targetTable.getDbDefault());
        }

        // 比较额外信息
        if (!sourceTable.getDbExtra().equals(targetTable.getDbExtra())) {
            differences.add(sourceTable.getDbField() + "额外信息" + ": source = " + sourceTable.getDbExtra() + ", target = " + targetTable.getDbExtra());
        }
        return differences;
    }
}




