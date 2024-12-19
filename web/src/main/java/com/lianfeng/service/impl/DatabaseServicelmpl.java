package com.lianfeng.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.listenner.NoModelDataListener;
import com.lianfeng.mapper.DatabaseMapper;
import com.lianfeng.service.IDatabaseService;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;



@Service
public class DatabaseServicelmpl extends ServiceImpl<DatabaseMapper, Object> implements IDatabaseService {
    public static String TABLE_NAME = null;//表名字
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
    @Transactional
    public String uploadExcel(MultipartFile file,String name) {
        String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);

        TABLE_NAME = name;//表名字
        //读取第一个sheet 同步读取会自动finish
        //用EasyExcel实现读数据，在监听器中存入到数据库中
        //com.lianfeng.listenner.NoModelDataListener 监听器的路径
        EasyExcel.read(absolutePath, new NoModelDataListener())
                .sheet()
                .doRead();

        //还要把不同字段不同的表
        return absolutePath;
    }

    /**********************************private**********************************/

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




