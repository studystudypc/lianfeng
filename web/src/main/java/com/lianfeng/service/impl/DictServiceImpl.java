package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;
import com.lianfeng.common.utils.JsonUtiles;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.model.entity.Dict;
import com.lianfeng.mapper.DictMapper;
import com.lianfeng.po.DictPoToExcel;
import com.lianfeng.service.IDictService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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
    @Override
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
    }



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
    @Override
    @Transactional
    public String uploadExcel(MultipartFile file) {
       String absolutePath = uploadFileToLocal(file.getOriginalFilename(), file);

        List<Dict> dicts = readLocalExcel(absolutePath);

        for (Dict dict : dicts) {
            saveOrUpdate(dict);
        }
        return absolutePath;
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
    private List<Dict> readLocalExcel(String path) {
        ExcelListenner<Dict> listener = new ExcelListenner<>();
        readExcel(path, Dict.class, listener);
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




