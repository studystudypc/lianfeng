package com.lianfeng.common.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static cn.hutool.poi.excel.cell.CellUtil.getCellValue;
import static com.lianfeng.common.constants.LFanConstants.NAME_XLS;
import static com.lianfeng.common.constants.LFanConstants.NAME_XLSX;

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

    /**
     * 读入excel文件，解析后返回
     * @param file MultipartFile对象
     * @return 包含每行数据的列表
     * @throws IOException 文件读取异常
     */
   /* public static List<String[]> readExcel(MultipartFile file)  {
        try {
            checkFile(file);
        } catch (IOException e) {
            throw new LFBusinessException("文件不存在");
        }
        Workbook workbook = null;
        try {
            workbook = getWorkBook(file);
        } catch (IOException e) {
            throw new LFBusinessException("不支持的文件格式");
        }
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
            try {
                for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    if (sheet == null) continue;

                    int firstRowNum = sheet.getFirstRowNum();
                    int lastRowNum = sheet.getLastRowNum();

                    for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                        Row row = sheet.getRow(rowNum);
                        if (row == null) continue;

                        int firstCellNum = row.getFirstCellNum();
                        int lastCellNum = row.getLastCellNum();
                        String[] cells = new String[lastCellNum];

                        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                            Cell cell = row.getCell(cellNum);
                            cells[cellNum] = getCellValue(cell);
                        }
                        list.add(cells);
                    }
                }
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    throw new LFBusinessException("关闭失败");
                }
            }
        }
        return list;
    }*/
    public static List<String[]> readExcel(MultipartFile file) {
        try {
            checkFile(file);
        } catch (IOException e) {
            throw new LFBusinessException("文件不存在");
        }
        Workbook workbook = null;
        try {
            workbook = getWorkBook(file);
        } catch (IOException e) {
            throw new LFBusinessException("不支持的文件格式");
        }
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
            try {
                for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                    Sheet sheet = workbook.getSheetAt(sheetNum);
                    if (sheet == null) continue;

                    Iterator<Row> rowIterator = sheet.iterator();
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        if (row == null) continue;

                        int firstCellNum = row.getFirstCellNum();
                        int lastCellNum = row.getLastCellNum();
                        String[] cells = new String[lastCellNum - firstCellNum];

                        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                            Cell cell = row.getCell(cellNum);
                            cells[cellNum - firstCellNum] = getCellValue(cell);
                        }
                        list.add(cells);
                    }
                }
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    throw new LFBusinessException("关闭失败");
                }
            }
        }
        return list;
    }

    /**********************************private**********************************/

    private static void checkFile(MultipartFile file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException("文件不存在！");
        }
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith(NAME_XLS) && !fileName.endsWith(NAME_XLSX)) {
            throw new IOException(fileName + " 不是excel文件");
        }
    }

    private static Workbook getWorkBook(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Workbook workbook;
        try (InputStream is = file.getInputStream()) {
            if (fileName.endsWith(NAME_XLS)) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(NAME_XLSX)) {
                workbook = new XSSFWorkbook(is);
            } else {
                throw new IOException("不支持的文件格式");
            }
        }
        return workbook;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            cell.setCellType(CellType.STRING);
        }
        String cellValue;
        switch (cell.getCellType()) {
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK:
                cellValue = "";
                break;
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

}
