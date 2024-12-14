package com.lianfeng.common.listenner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.8
 * @注释  关于Excel监听器
 * @Author liuchuanping
 * @Date 2024-12-14 21:42
 */

/**
 * // 使用ExcelListenner读取Excel文件
 * ExcelListenner<User> listener = new ExcelListenner<>();
 * ExcelUtils.readExcel("path/to/excel/file.xlsx", User.class, listener);
 *
 * // 获取读取的数据
 * List<User> users = listener.getDataList();
 * // 现在users列表中包含了Excel文件中的每一行数据对应的User对象
 */

@Slf4j
public class ExcelListenner<T> extends AnalysisEventListener<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExcelListenner.class);

    /**
     *
     * Excel数据临时存储
     */
    private List<T> dataList = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        dataList.add(t);
        logger.info("读取到数据: {}", t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        logger.info("所有数据已读取完成，读取到的数据数量: {}", dataList.size());
    }

    // 获取读取到的数据
    public List<T> getDataList() {
        return dataList;
    }


}
