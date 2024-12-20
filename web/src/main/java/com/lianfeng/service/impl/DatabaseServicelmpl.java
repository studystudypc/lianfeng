package com.lianfeng.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.utils.ExcelUtils;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.listenner.NoModelDataListener;
import com.lianfeng.mapper.DatabaseMapper;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.vo.DatabaseVo;
import jdk.nashorn.internal.objects.NativeNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


@Service
public class DatabaseServicelmpl extends ServiceImpl<DatabaseMapper, Object> implements IDatabaseService {
    public static String TABLE_NAME = null;//表名字

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Autowired
//    private SourceConfiguration sourceConfiguration;//源数据库配置
//
//    @Autowired
//    private TargetConfiguration targetConfiguration;//目标数据库配置


    /**
     * @return 绝对路径名字
     * @Author liuchuanping
     * @Description
     * 1.上传到本地
     * 2.读取Excel文件内容
     * 3.把Excel文件内容转为Map格式，
     * 默认Excel表中第一个数据为K，其它值为v
     * <p>
     * 4.存入数据库对应的数据库中
     * @Date 2024-12-17 13:53
     * @Param file
     **/
    @Transactional
    public DatabaseVo uploadExcel(MultipartFile file, String name, String idName) {

        DatabaseVo databaseVo = new DatabaseVo();//返回的数据值

        String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);
        databaseVo.setAbsolutePath(absolutePath);

        StringBuilder sql = new StringBuilder("REPLACE INTO ").append(name).append(" (");//有值的数据sql

        // 读取 Excel 文件
        List<String[]> readExcelList = ExcelUtils.readExcel(file);
        databaseVo.setExcelContent(readExcelList);
        databaseVo.setNumExcel(readExcelList.size() - 1);

        List<Map<String, String>> nullIdName = new ArrayList<>();  // 存放主键为空的数据
        List<Map<String, String>> jsonList = new ArrayList<>();    // 存放有效的数据

        // 获取表头字段名
        String[] headerName = readExcelList.get(0);
        int index = -1;  // 主键列的索引

        // 查找主键在表头中的位置
        for (int i = 0; i < headerName.length; i++) {
            if (idName.equals(headerName[i])) {
                index = i;
                break;
            }
        }

        // 如果找不到主键，抛出异常
        if (index == -1) {
            throw new LFBusinessException("指定的 主键 未在表头中找到。");
        }

        // 拼接表头字段
        sql.append(String.join(", ", headerName)).append(") VALUES ");

        boolean hasValidData = false;  // 标记是否有有效数据

        // 遍历 Excel 数据的每一行
        for (int i = 1; i < readExcelList.size(); i++) {
            String[] row = readExcelList.get(i);
            Map<String, String> jsonMap = new HashMap<>();
            Map<String, String> nullJsonMap = new HashMap<>();

            boolean isIdNameEmpty = row.length <= index || row[index] == null || row[index].trim().isEmpty();

            // 拼接当前行的值
            if (!isIdNameEmpty) {
                StringBuilder sqlValue = new StringBuilder("(");
                for (int j = 0; j < headerName.length; j++) {
                    String value = j < row.length && row[j] != null ? row[j].replace("'", "''") : "NULL";
                    sqlValue.append("'").append(value).append("'");
                    if (j < headerName.length - 1) {
                        sqlValue.append(", ");
                    }
                    jsonMap.put(headerName[j], value);
                }
                sqlValue.append(")");

                if (hasValidData) {
                    sql.append(", ");
                }
                sql.append(sqlValue);
                hasValidData = true;

                jsonList.add(jsonMap);
            } else {
                // 主键为空，存入 nullIdName
                for (int j = 0; j < headerName.length; j++) {
                    String value = j < row.length && row[j] != null ? row[j] : "NULL";
                    nullJsonMap.put(headerName[j], value);
                }
                nullIdName.add(nullJsonMap);
            }
        }
        //插入存在主键的数据，使用的是批量插入
//        jdbcTemplate.execute(sql.toString());
        int update = jdbcTemplate.update(sql.toString());
        databaseVo.setNumValue(update);
        Integer nullNum = handleNullJson(name, idName, headerName, nullIdName);
        databaseVo.setNullValue(nullNum);
        Integer numValue = databaseVo.getNumValue();
        databaseVo.setSumNums(nullNum+numValue);

        return databaseVo;
    }

    /**
     * 处理json主键为空的字段
     *
     * @param name
     * @param idName
     * @param headerName
     * @param nullIdName
     */
    private Integer handleNullJson(String name, String idName, String[] headerName, List<Map<String, String>> nullIdName) {
        Integer num = 0;
        for (Map<String, String> nullJsonMap : nullIdName) {
            StringBuilder selectSql = new StringBuilder("SELECT ").append(idName).append(" FROM ").append(name).append(" WHERE ");

            // 处理掉主键字段
            List<String> headerList = new ArrayList<>(Arrays.asList(headerName));
            headerList.remove(idName);
            String[] handleHeaderName = headerList.toArray(new String[0]); // 已经处理的主键表头

            boolean isFirstCondition = true;
            //拼接查询
            for (String header : handleHeaderName) {
                String value = nullJsonMap.get(header);
                if (value != null && !value.isEmpty()) {
                    if (isFirstCondition) {
                        selectSql.append(header).append(" = '").append(value.replace("'", "''")).append('\'');
                        isFirstCondition = false;
                    } else {
                        selectSql.append(" AND ").append(header).append(" = '").append(value.replace("'", "''")).append('\'');
                    }
                }
            }
            // 执行查询
            //查询的结果
            List<Map<String, Object>> results = jdbcTemplate.queryForList(selectSql.toString());
            if (results.isEmpty()) {
                // 构建插入语句
                StringBuilder insertSql = new StringBuilder("INSERT INTO ").append(name).append(" (").append(String.join(", ", handleHeaderName)).append(") VALUES (");
                for (String header : handleHeaderName) {
                    String value = nullJsonMap.get(header);
                    insertSql.append("'").append(value != null ? value.replace("'", "''") : "NULL").append("'");
                    if (!header.equals(headerName[headerName.length - 1])) {
                        insertSql.append(", ");
                    }
                }
                insertSql.append(");");
                //插入语句
                jdbcTemplate.update(insertSql.toString());
                num++;
            } else {
                // 构建更新语句
                // 获取查询结果中的第一个记录的dict_id值
                Map<String, Object> result = results.get(0);
                String dictIdValue = result.get(idName).toString();
                // 构建更新语句
                StringBuilder updateSql = new StringBuilder("UPDATE ").append(name).append(" SET ");
                for (int i = 0; i < handleHeaderName.length; i++) {
                    if (i > 0) {
                        updateSql.append(", ");
                    }
                    String value = nullJsonMap.get(handleHeaderName[i]);
                    updateSql.append(handleHeaderName[i]).append(" = '").append(value != null ? value.replace("'", "''") : "").append('\'');
                }
                updateSql.append(" WHERE ").append(idName).append(" = '").append(dictIdValue.replace("'", "''")).append('\'');
                // 执行更新语句
                jdbcTemplate.execute(updateSql.toString());
                num++;
            }
        }
        return num;
    }


//
//    /**
//     * @Author liuchuanping
//     * @Description
//     * 比较数据库结构
//     * 获取源 sql 文件的表字段信息
//     * 获取目标 sql 文件的表字段信息
//     * 比较两个 sql 文件的字段信息
//     * @Date 2024-12-19 10:48
//     * @Param sourceTableName
//     * @param targetTableName
//     * @return
//     **/
//    @Override
//    public void compareDB(String sourceTableName, String targetTableName) {
//        try {
//            Connection connection = sourceConfiguration.getConnection();
//            List<Map<String, Object>> sourceData = fetchTableData(connection,sourceTableName);
//
//            System.out.println(sourceData);
//        } catch (SQLException e) {
//            throw new LFBusinessException("数据库连接异常");
//        }
//
//        try {
//            Connection connection = targetConfiguration.getConnection();
//            List<Map<String, Object>> targetData = fetchTableData(connection,targetTableName);
//            System.out.println(targetData);
//        } catch (SQLException e) {
//            throw new LFBusinessException("数据库连接异常");
//        }
//    }


    private List<Map<String, Object>> fetchTableData(Connection connection, String tableName) throws SQLException {
        List<Map<String, Object>> tableData = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    Object columnValue = resultSet.getObject(i);
                    row.put(columnName, columnValue);
                }
                tableData.add(row);
            }
        }
        return tableData;
    }

    /**********************************private**********************************/

    /**
     * 判断系统
     * 生成路径
     * 上传文件
     *
     * @param fileName 文件名字
     * @param file     文件
     * @return 文件的绝对路径
     */
    private String uploadFileToLocal(String fileName, MultipartFile file) {
        String targetDir = DictConstants.DICT_PATH;
        String osName = System.getProperty("os.name").toLowerCase();

        // 根据操作系统调整路径分隔符
        if (!osName.contains("win")) {
            targetDir = targetDir.replace("/", "\\");
        } else {
            targetDir = targetDir.replace("\\", "/");
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = new Date();
        String formattedDate = dateFormat.format(now);

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i);
            fileName = fileName.substring(0, i);
        }
        fileName += "." + formattedDate + extension;

        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 如果目录不存在，则创建目录
        }

        File targetFile = new File(dir, fileName);

        if (targetFile.exists()) {
            throw new LFBusinessException("点击请勿频繁，请稍等重试");
        }

        try {
            // 使用Files.copy保存MultipartFile到文件系统中
            Path filePath = Paths.get(targetFile.getAbsolutePath());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new LFBusinessException("文件上传失败");
        }

        return targetFile.getAbsolutePath(); // 返回文件的绝对路径
    }

}




