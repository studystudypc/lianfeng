package com.lianfeng.common.listenner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.8
 * @注释  关于Excel监听器
 * @Author liuchuanping
 * @Date 2024-12-14 21:42
 */
@Slf4j
public class ExcelListenner<T> extends AnalysisEventListener<T> {
    /**
     * Excel数据临时存储
     */
    private List<T> dataList = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        dataList.add(t);
        log.info("读取到数据: {}", t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据已读取完成，读取到的数据数量: {}", dataList.size());
    }

    // 获取读取到的数据
    public List<T> getDataList() {
        return dataList;
    }
}