package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.utils.ExcelUtils;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.mapper.DatabaseMapper;
import com.lianfeng.service.IDatabaseService;
import com.lianfeng.po.DatabasePo;
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
import java.util.stream.Collectors;


@Service
public class DatabaseServicelmpl extends ServiceImpl<DatabaseMapper, Object> implements IDatabaseService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * @return 绝对路径名字
     * @return DatabaseVo
     * @Author liuchuanping
     * @Description 上传文件到本地，并存入到数据库
     * @Date 2024-12-17 13:53
     * @Param file
     **/
    @Transactional
    public DatabasePo uploadExcel(MultipartFile file, String name, String idName) {

        DatabasePo databasePo = new DatabasePo();//返回的数据值

        String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);
        databasePo.setAbsolutePath(absolutePath);

        StringBuilder sql = new StringBuilder("REPLACE INTO ").append(name).append(" (");//有值的数据sql

        // 读取 Excel 文件
        List<String[]> clearExcelList = ExcelUtils.readExcel(file);
        // 把Excel文件空内容全部舍弃
        List<String[]> readExcelList = new ArrayList<>();
        for (String[] row : clearExcelList) {
            boolean isEmpty = true;
            for (String cell : row) {
                if (cell != null && !cell.trim().isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                readExcelList.add(row);
            }
        }

        databasePo.setExcelContent(readExcelList);
        databasePo.setNumExcel(readExcelList.size() - 1);

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
        int update = jdbcTemplate.update(sql.toString());
        databasePo.setNumValue(update);
        Integer nullNum = handleNullJson(name, idName, headerName, nullIdName);
        databasePo.setNullValue(nullNum);
        Integer numValue = databasePo.getNumValue();
        databasePo.setSumNums(nullNum + numValue);

        return databasePo;
    }

    /**
     * @return 表头
     * @Author liuchuaning
     * @Description 从文件读表头
     * @Date 2024-12-21 10:06
     * @Param file
     **/
    @Override
    public DatabasePo returnReverso(MultipartFile file) {
        DatabasePo databasePo = new DatabasePo();
        List<String[]> readExcelList = ExcelUtils.readExcel(file);
        String[] headerName = readExcelList.get(0);

        databasePo.setReversoName(headerName);
        return databasePo;
    }

    /**
     * @param tableName
     * @param field
     * @return DatabasePo
     * @Author liuchuaning
     * @Description 多字段更新
     * @Date 2024-12-22 14:11
     * @Param file
     **/
    @Override
    public DatabasePo updateExcel(MultipartFile file, String tableName, String[] keyName, String[] field) {
        DatabasePo databasePo = returnReverso(file);
        String[] reversoName = databasePo.getReversoName(); // 表头的名字

        // 读取 Excel 文件
        List<String[]> clearExcelList = ExcelUtils.readExcel(file);
        // 把Excel文件空内容全部清除
        List<String[]> readExcelList = new ArrayList<>();

        for (String[] row : clearExcelList) {
            boolean isEmpty = true;
            for (String cell : row) {
                if (cell != null && !cell.trim().isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                readExcelList.add(row);
            }
        }

        List<String> sqlList = new ArrayList<>(); // 存放完整的SQL语句

        // 多主键列的索引
        String[] keyIndex = new String[keyName.length];
        for (int i = 0; i < reversoName.length; i++) {
            for (int j = 0; j < keyName.length; j++) {
                if (reversoName[i].equals(keyName[j])) {
                    keyIndex[j] = String.valueOf(i);
                    break;
                }
            }
        }

        // 如果找不到主键，抛出异常
        boolean flag = true;
        for (String index : keyIndex) {
            if (index != null && !index.isEmpty()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            throw new LFBusinessException("指定的 主键 未在表头中找到。");
        }

        int[] indices = new int[field.length]; // filed与reversoName对应的下标

        // 初始化field 字段值的索引
        for (int i = 0; i < field.length; i++) {
            indices[i] = -1;
        }
        // 遍历reversoName数组，查找field中的元素对应的索引
        for (int i = 0; i < reversoName.length; i++) {
            for (int j = 0; j < field.length; j++) {
                if (reversoName[i].equals(field[j])) {
                    indices[j] = i;
                    break;
                }
            }
        }

        List<String[]> nullExcelContent = new ArrayList<>(); // 存放空的主键值的行

        for (int rowIndex = 1; rowIndex < readExcelList.size(); rowIndex++) { // 循环每一个excel文件内容
            String[] values = readExcelList.get(rowIndex);
            boolean isAccountEmpty = values[Integer.parseInt(keyIndex[1])] == null || values[Integer.parseInt(keyIndex[1])].trim().isEmpty();
            boolean isDictIdEmpty = values[Integer.parseInt(keyIndex[0])] == null || values[Integer.parseInt(keyIndex[0])].trim().isEmpty();

            if (isAccountEmpty || isDictIdEmpty) {
                nullExcelContent.add(values);
                continue; // 继续检查下一行数据
            }

            // 构建完整的SQL语句
            StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
            for (int i = 0; i < field.length; i++) { // 包括所有字段
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(reversoName[indices[i]])
                        .append(" = '")
                        .append(values[indices[i]].replace("'", "''")) // 防止SQL注入，对单引号进行转义
                        .append("'");
            }

            // 添加WHERE子句
            sql.append(" WHERE ").append(keyName[0])
                    .append(" = '")
                    .append(values[Integer.parseInt(keyIndex[0])].replace("'", "''"))
                    .append("' AND ")
                    .append(keyName[1])
                    .append(" = '")
                    .append(values[Integer.parseInt(keyIndex[1])].replace("'", "''"))
                    .append("'");

            // 将构建的SQL语句添加到列表中
            sqlList.add(sql.toString());
        }

        int sum = 0;//计数
        // 执行数据库更新
        for (int i = 0; i < sqlList.size(); i++) {
            jdbcTemplate.update(sqlList.get(i));
            sum++;
        }

        databasePo.setNullValue(nullExcelContent.size());
        databasePo.setNumExcel(readExcelList.size() - 1);
        databasePo.setNumValue(sum);
        databasePo.setNullExcelContent(nullExcelContent);
        return databasePo;
    }

    /**
     * 多字段用户选择更新
     *
     * @param file
     * @param tableName
     * @param keyName
     * @param field
     * @return
     */
    @Override
    public DatabasePo moreUpdateField(MultipartFile file, String tableName, String[] keyName, String[] field) {
        DatabasePo databasePo = returnReverso(file);
        String[] reversoName = databasePo.getReversoName(); // 表头的名字

        // 读取 Excel 文件
        List<String[]> clearExcelList = ExcelUtils.readExcel(file);

        // 清除空行
        List<String[]> readExcelList = new ArrayList<>();
        for (String[] row : clearExcelList) {
            boolean isEmpty = true;
            for (String cell : row) {
                if (cell != null && !cell.trim().isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }
            if (!isEmpty) {
                readExcelList.add(row);
            }
        }

        // 确定主键列的索引
        int[] keyIndexes = new int[keyName.length];
        for (int i = 0; i < keyName.length; i++) {
            keyIndexes[i] = -1;
            for (int j = 0; j < reversoName.length; j++) {
                if (keyName[i].equals(reversoName[j])) {
                    keyIndexes[i] = j;
                    break;
                }
            }
        }

        // 验证主键是否存在于表头
        for (int index : keyIndexes) {
            if (index == -1) {
                throw new LFBusinessException("指定的 主键 未在表头中找到。");
            }
        }

        // 清除主键为空的行
        List<String[]> clearKeyExcelList = new ArrayList<>();
        List<String[]> nullKeyExcelList = new ArrayList<>();
        for (String[] values : readExcelList) {
            boolean isKeyEmpty = false;
            for (int keyIndex : keyIndexes) {
                if (values[keyIndex] == null || values[keyIndex].trim().isEmpty()) {
                    isKeyEmpty = true;
                    break;
                }
            }
            if (!isKeyEmpty) {
                clearKeyExcelList.add(values);
            }else {
                nullKeyExcelList.add(values);
            }
        }

        // 构建 SQL 语句
        List<String> sqlList = new ArrayList<>();
        for (int j = 1; j < clearKeyExcelList.size(); j++) {
            String[] values = clearKeyExcelList.get(j);

            StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");

            // 构建字段部分
            for (int i = 0; i < reversoName.length; i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append(reversoName[i]);
            }

            sql.append(") VALUES (");

            // 构建值部分
            for (int i = 0; i < reversoName.length; i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                String value = values[i] == null ? "" : values[i].replace("'", "''");
                sql.append("'").append(value).append("'");
            }

            sql.append(") ON DUPLICATE KEY UPDATE ");

            // 构建更新部分
            for (int i = 0; i < field.length; i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                String[] fieldParts = field[i].split("=");
                if (fieldParts.length != 2) {
                    throw new LFBusinessException("字段格式错误，必须为 key=value 格式");
                }
                String fieldName = fieldParts[0].trim();
                String fieldValue = fieldParts[1].trim();
                sql.append(fieldName)
                        .append(" = '")
                        .append(fieldValue.replace("'", "''"))
                        .append("'");
            }

            sqlList.add(sql.toString());
        }

        // 执行 SQL
        int sum = 0;
        for (String sqlStr : sqlList) {
            jdbcTemplate.update(sqlStr);
            sum++;
        }

        // 设置返回结果
        databasePo.setNumExcel(readExcelList.size() - 1); // 去除表头行
        databasePo.setNumValue(sum);
        databasePo.setNullExcelContent(nullKeyExcelList);
        return databasePo;
    }

    /**********************************private**********************************/

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
            List<Map<String, Object>> results = jdbcTemplate.queryForList(selectSql.toString());
            if (results.isEmpty()) {
                // 构建插入语句
                StringBuilder insertSql = new StringBuilder("INSERT INTO ").append(name).append(" (").append(String.join(", ", handleHeaderName)).append(") VALUES (");
                for (String header : handleHeaderName) {
                    String value = nullJsonMap.get(header);
                    insertSql.append("'").append(value != null ? value.replace("'", "''") : "NULL").append("'");
                    if (!header.equals(handleHeaderName[handleHeaderName.length - 1])) {
                        insertSql.append(", ");
                    }
                }
                insertSql.append(");");
                jdbcTemplate.update(insertSql.toString());
                num++;
            } else {
                // 构建更新语句
                Map<String, Object> result = results.get(0);
                String dictIdValue = result.get(idName).toString();
                StringBuilder updateSql = new StringBuilder("UPDATE ").append(name).append(" SET ");
                for (int i = 0; i < handleHeaderName.length; i++) {
                    if (i > 0) {
                        updateSql.append(", ");
                    }
                    String value = nullJsonMap.get(handleHeaderName[i]);
                    updateSql.append(handleHeaderName[i]).append(" = '").append(value != null ? value.replace("'", "''") : "").append('\'');
                }
                updateSql.append(" WHERE ").append(idName).append(" = '").append(dictIdValue.replace("'", "''")).append('\'');
                jdbcTemplate.execute(updateSql.toString());
                num++;
            }
        }
        return num;
    }

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




