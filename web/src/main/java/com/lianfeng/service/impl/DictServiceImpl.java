package com.lianfeng.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.model.entity.Account;
import com.lianfeng.model.entity.Dict;
import com.lianfeng.mapper.DictMapper;
import com.lianfeng.po.DictPoToExcel;
import com.lianfeng.service.IDictService;
import org.apache.ibatis.jdbc.SQL;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.sql.SQLData;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.lianfeng.common.utils.ExcelUtils.readExcel;

/**
 * @author LCP
 * @description 针对表【dict】的数据库操作Service实现
 * @createDate 2024-12-12 21:30:58
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
        implements IDictService {

    @Autowired
    private DictMapper dictMapper;


    /**
     * @return
     * @Author liuchuanping
     * @Description
     * 文件检查
     *  1.先比较文件大小
     *
     *  2.比较文件字节
     *
     *  3.比较文件内容与数据库内容是否相等
     *
     *  4.删除文件内容
     * @Date 2024-12-15 22:44
     * @param absolutePath
     * @param file
     **/
   /* @Override
    @Transactional
    public void checkFile(String absolutePath,MultipartFile file) {
        if (!compareFiles(file,absolutePath)) {
            throw new LFBusinessException("文件内容不匹配");
        }
        List<Dict> listExcel = readLocalExcel(absolutePath);//读取的excel文件内容
        List<DictPoToExcel> dictPoToExcelList = new ArrayList<>();//实体转换存入的数组

        for (Dict dict : listExcel) {
            DictPoToExcel dictPoToExcel = new DictPoToExcel();
            try {
                BeanUtils.copyProperties(dict,dictPoToExcel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dictPoToExcelList.add(dictPoToExcel);
        }
        List<Dict> dbDicts  = list();//数据库中数据
        List<DictPoToExcel> dictPoToDB = new ArrayList<>();//实体转换存入的数组
        for (Dict dict : dbDicts) {
            DictPoToExcel dictPoToExcel = new DictPoToExcel();
            try {
                BeanUtils.copyProperties(dict,dictPoToExcel);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dictPoToDB.add(dictPoToExcel);
        }

        if (!compareDictLists(dictPoToExcelList, dictPoToDB)) {
            throw new LFBusinessException("数据文件内容与Excel文件内容不一样");
        }
        //删除本地文件内容
        clearLocalFile(absolutePath);
    }*/

    /**
     * @return
     * @Author liuchuaning
     * @Description
     *  上传文件内容，为后续判断文件是否出错做准备
     *  读取Excel文件内容
     *  把Excel文件内容转为entity格式
     *  存入数据库
     * @Date 2024-12-16 10:05
     * @Param file
     **/
    /*@Override
    @Transactional
    public String uploadExcel(MultipartFile file) {
        String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);

        List<Dict> dicts = readLocalExcel(absolutePath);

        for (Dict dict : dicts) {
            saveOrUpdate(dict);
        }
        return absolutePath;
    }*/


    @Override
    public void checkFile(String absolutePath, MultipartFile file) {

    }

    /**
     * @Author liuchuanping
     * @Description
     *  1.上传到本地
     *  2.读取Excel文件内容
     *  3.把Excel文件内容转为Json格式，
     *      默认Excel表中第一个数据为K，其它值为v
     *
     *  4.存入数据库对应的数据库中
     * @Date 2024-12-17 13:53
     * @Param file
     * @return 绝对路径名字
     **/
    @Override
    @Transactional
    public String uploadExcel(MultipartFile file,String name) {
        String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);

        List<String[]> strings = readExcel(file);//读出来的Excel文件

        String stringToJson = convertToJSON(strings);//把文件转为json

        saveToDatabase(stringToJson, name);

        //在这里写一条通用的sql，把对应的数据放入到数据库中
        //假设我上传第一个文件为这个数据
        // [{"dict_id":"5025","vendor":"","key_words":"ADHVACPARTS","account":"Purchases"},{"dict_id":"4959","vendor":"","key_words":"AIRGAS","account":"Purchases"},{"dict_id":"1294","vendor":"CRITTERFENCE","key_words":"CRITTERFENCE","account":"Purchases"},{"dict_id":"1297","vendor":"CS PACKAGING","key_words":"CS PACKAGING","account":"Purchases"},{"dict_id":"1296","vendor":"EMODAL.COM","key_words":"EMODAL.COM","account":"Purchases"},{"dict_id":"5023","vendor":"","key_words":"GUSTAVE A LARSON","account":"Purchases"},{"dict_id":"4984","vendor":"","key_words":"HUSSMANN","account":"Purchases"},{"dict_id":"4912","vendor":"","key_words":"ICE MAKERS","account":"Purchases"},{"dict_id":"1324","vendor":"IMPORTGENIUS.COM","key_words":"IMPORTGENIUS.COM","account":"Marketing"},{"dict_id":"550","vendor":"Sunlight Kitchen Hardware Supplies","key_words":"KITCHEN & HARDWA","account":"Purchases"},{"dict_id":"2285","vendor":"","key_words":"LAS VEGAS CONVENTION","account":"Marketing"},{"dict_id":"5001","vendor":"","key_words":"LOBSTERBOYS","account":"Purchases"},{"dict_id":"4988","vendor":"","key_words":"MANAU FOOD EQUIPMENT","account":"Purchases"},{"dict_id":"1315","vendor":"PAKRITE SYSTEMS","key_words":"PAKRITE SYSTEMS","account":"Purchases"},{"dict_id":"4930","vendor":"","key_words":"PARTS TOWN LLC","account":"Purchases"},{"dict_id":"4990","vendor":"","key_words":"PartSelect","account":"Supplies"},{"dict_id":"542","vendor":"PRO ACOUSTICS LLC","key_words":"PRO ACOUSTICS","account":"Supplies"},{"dict_id":"4999","vendor":"","key_words":"Schindler Elevator","account":"Purchases"},{"dict_id":"2692","vendor":"SHEPARD EXPOSITION","key_words":"SHEPARD EXPOSITION","account":"Marketing"},{"dict_id":"4931","vendor":"","key_words":"THE WEBSTAURANT STORE","account":"Purchases"},{"dict_id":"4841","vendor":"TONA BATHROOM VANITY INC","key_words":"TONA BATHROOM VANITY INC","account":"Purchases"},{"dict_id":"4855","vendor":"TONA SANITARY WA","key_words":"TONA SANITARY WA","account":"Purchases"},{"dict_id":"5005","vendor":"","key_words":"VERSACART SYSTEMS","account":"Purchases"},{"dict_id":"5006","vendor":"","key_words":"VESTIS SERVICES LLC","account":"Purchases"},{"dict_id":"4919","vendor":"","key_words":"WISMETTAC ASIAN WISMETTAC","account":"Purchases"},{"dict_id":"","vendor":"","key_words":"","account":""}]
        // 把他存入到对应的数据库dict

        //假设我上传第二个文件为这个数据
        //[{"account_id":"5025","account_code":"","account_name":"ADHVACPARTS","account":"Purchases"},{"account_id":"4959","account_code":"","account_name":"AIRGAS","account":"Purchases"},{"account_id":"1294","account_code":"CRITTERFENCE","account_name":"CRITTERFENCE","account":"Purchases"}]
        // 把他存入到对应的数据库account

        //还要把不同字段不同的表
        return absolutePath;
    }

    /**
     * 1.通过名字获取，数据库中所有字段
     * 2.通过json数据写sql传入数据库
     * @param jsonData json数据
     * @param name 数据库表名
     */
    private void saveToDatabase(String jsonData, String name) {

    }


    /**
     *  3.把Excel文件内容转为Json格式，
     *      默认Excel表中第一个数据为K，其它值为v
     * @param strings
     * @return Json串
     */
    private String convertToJSON(List<String[]> strings) {
        if (strings == null || strings.isEmpty()) {
            return "{}";
        }
        String[] headers = strings.get(0);

        JSONArray jsonArray = new JSONArray();

        for (int i = 1; i < strings.size(); i++) {
            String[] row = strings.get(i);
            JSONObject jsonObject = new JSONObject();

            for (int j = 0; j < headers.length; j++) {
                if (j < row.length) {
                    jsonObject.put(headers[j], row[j]);
                } else {
                    jsonObject.put(headers[j], "");
                }
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }


    /**********************************private**********************************/

    /**
     * @return
     * @Author liuchuaning
     * @Description
     * 比较文件内容与数据库中内容是否相等
     * @Date 2024-12-17 10:56
     * @Param dictPoToExcelList
     * @param dictPoToDB
     * @return boolean
     **/
    private boolean compareDictLists(List<DictPoToExcel> dictPoToExcelList, List<DictPoToExcel> dictPoToDB) {
        String dictPoToExcelListString = dictPoToExcelList.toString().replaceAll(",\\s*dictId=\\d+", "");
        List<DictPoToExcel> dblist = dictPoToDB;
        for (DictPoToExcel dictPoToExcel : dblist) {
            String toExcelString = dictPoToExcel.toString();
            if (toExcelString.contains(dictPoToExcelListString)){
                throw new LFBusinessException("数据文件内容与Excel文件内容不一样");
            }
        }
        return true;
    }

    /**
     * @return *
     * @Author liuchuaning
     * @Description
     * 比较上传的文件内容和本地文件内容是否一样
     * @Date 2024-12-16 14:15
     * @Param
     * @return true
     **/
    private boolean compareFiles(MultipartFile file, String absolutePath) {
        try {
            // 确保上传的文件不为空
            if (file.isEmpty()) {
                throw new IllegalArgumentException("上传的文件不能为空");
            }

            // 读取上传文件的内容到字节数组中
            byte[] uploadedFileContent = file.getBytes();

            // 读取本地文件的内容
            Path path = Paths.get(absolutePath);
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                throw new IllegalArgumentException("本地文件不存在或不是一个文件");
            }
            byte[] localFileContent = Files.readAllBytes(path);

            // 比较两个文件的内容是否相同
            return Arrays.equals(uploadedFileContent, localFileContent);
        } catch (IOException e) {
            // 处理读取文件时的异常
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 删除Excel文件
     */
    private void clearLocalFile(String path) {
        try {
            Path filePath = Paths.get( path);
            Files.delete(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LFBusinessException("文件删除失败");
        }
    }


    /**
     * 读Excel文件
     *
     * @param path
     */
    private List<T> readLocalExcel(String path,Class<T> entity) {
        ExcelListenner<T> listener = new ExcelListenner<>();
        readExcel(path, entity, listener);
        return listener.getDataList();
    }

    /**
     * 判断系统
     * 生成路径
     * 上传文件
     * @param fileName 文件名字
     * @param file 文件
     * @return 文件的绝对路径
     */
    private String uploadFileToLocal(String fileName,MultipartFile file) {
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




