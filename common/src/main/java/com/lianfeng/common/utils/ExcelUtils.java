package com.lianfeng.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;

import java.io.File;
import java.io.IOException;

/**
 * @version 1.8
 * @注释  Excel文件工具类
 * @Author liuchuanping
 * @Date 2024-12-14 21:11
 */
public class ExcelUtils {

    /**
     * @Author liuchuanping
     * @Description 通用方法读取Excel文件内容
     * @Date 2024-12-14 21:35
     * @param filePath    Excel文件路径
     * @param dataClass   Java对象类，该类的字段需与Excel列对应
     * @param readListener 读取监听器，用于处理读取到的数据
     * @param <T>         数据类型
     **/
    public static <T> void  readExcel(String filePath, Class<T> dataClass, ReadListener<T> readListener) {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("Excel文件路径为空");
            throw new LFBusinessException("Excel文件路径为空");
        }
        if (dataClass == null || readListener == null) {
            throw new LFBusinessException("监听器内容或实体对象为空");
        }

        try {
            EasyExcel.read(new File(filePath), dataClass, readListener).sheet().doRead();
//            EasyExcel.read("F:\\联丰\\lianfeng\\file\\path\\dict\\基础字典.xls", dataClass, readListener).sheet(0).doRead();
        } catch (Exception e) {
            throw new LFBusinessException("读取Excel文件时发生错误");
        }
    }

    /**
     * @Author liuchuanping
     * @Description 通用方法读取Excel文件内容(分页读取)
     * @Date 2024-12-14 21:41
     * @param filePath    Excel文件路径
     * @param dataClass   Java对象类，该类的字段需与Excel列对应
     * @param pageReadListener 分页读取监听器，用于处理读取到的数据
     * @param <T>         数据类型
     **/
    public static <T> void readExcelByPage(String filePath, Class<T> dataClass, PageReadListener<T> pageReadListener) {
        if (filePath == null || filePath.isEmpty()) {
            throw new LFBusinessException("Excel文件路径为空");
        }
        if (dataClass == null || pageReadListener == null) {
            throw new LFBusinessException("监听器内容或实体对象为空");
        }

        try {
            EasyExcel.read(new File(filePath), dataClass, pageReadListener).sheet().doRead();
        } catch (Exception e) {
            throw new LFBusinessException("读取Excel文件时发生错误");
        }
    }

    /**
     * @Author liuchuanping
     * @Description 把读取的数据转换为Json数据
     * @Date 2024-12-14 21:49
     * @Param filePath 文件路径
     * @Param dataClass 实体对象
     * @return json
     **/
    public static <T> String ExcelToJson(String filePath, Class<T> dataClass) {
        ExcelListenner<T> excelListenner = new ExcelListenner<>();
        readExcel(filePath, dataClass, excelListenner);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(excelListenner.getDataList());
        } catch (IOException e) {
            throw new LFBusinessException("Excel数据转换为json对象失败");
        }
    }

}
