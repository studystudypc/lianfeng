package com.lianfeng.listenner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.utils.JdbcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lianfeng.service.impl.DatabaseServicelmpl.TABLE_NAME;

@Slf4j
@Service
public class NoModelDataListener extends AnalysisEventListener<Map<Integer, String>> {


    // 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
    private static final int BATCH_COUNT = 5;
    private List<Map<Integer, String>> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    //存表头为数据库的字段
    private List<String> HeadList = new ArrayList<>();


    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        Map<Integer, String> cleanedData = new HashMap<>();
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            String value = entry.getValue();
            cleanedData.put(entry.getKey(), (value == null || value.trim().isEmpty()) ? "" : value);
        }

        log.info("解析到一条数据:{}", JSON.toJSONString(cleanedData));
        cachedDataList.add(cleanedData);

        if (cachedDataList.size() >= BATCH_COUNT) {
            // 清空列表以便再次使用
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    @Override
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        log.info("解析到的表头数据: {}", headMap);
        HeadList.clear(); // 清空之前的表头数据
        for (Object value : headMap.values()) {
            HeadList.add((String) value);
        }
    }

    /**
     * 把读取的数据存入到数据库中
     * 使用字符串拼接的方法
     * 用jdbc写入
     */

    private void saveData() {
        if (cachedDataList.isEmpty()) {
            throw new LFBusinessException("解析Excel文件内容失败");
        }

        StringBuilder sql = new StringBuilder("INSERT IGNORE INTO " + TABLE_NAME + " (");
        sql.append(String.join(", ", HeadList)).append(") VALUES ");

        for (Map<Integer, String> dataMap : cachedDataList) {
            StringBuilder sqlValue = new StringBuilder("(");
            for (String value : dataMap.values()) {
                // 添加值到sqlValue，并处理字符串中的单引号
                sqlValue.append("'").append(value == null ? "" : value.replace("'", "''")).append("',");
            }
            // 去掉最后一个逗号，并添加右括号
            if (sqlValue.length() > 1) {
                sqlValue.setLength(sqlValue.length() - 1); // 去掉最后的逗号
            }
            sqlValue.append(")");
            // 将当前记录的值添加到SQL语句中
            sql.append(sqlValue).append(", ");

        }
        // 去掉最后一个逗号和空格
        if (sql.length() > 0) {
            sql.setLength(sql.length() - 2); // 去掉最后的逗号和空格
        }

        try {
            JdbcUtil.update(sql.toString());
        }catch (LFBusinessException e){
            throw new LFBusinessException("数据库更新失败");
        }

    }
}
