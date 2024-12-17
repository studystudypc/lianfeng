package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;
import com.lianfeng.common.utils.FileUtils;
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
     * @Author liuchuaning
     * @Description * 文件上传接口，并把Excel数据存入数据库
     * 判断上传文件是否存在
     * 判断文件类似是不是xlm，xlms
     * 上传文件到本地路径,返回绝对路径
     * 读取Excel文件，并转换为Json
     * dict_id作为主键，不能重复，得判断dict_id是否为空，然后自定义赋值
     * 上传到数据库
     * 删除本地文件（因为业务只是要读取上传的Excel内容到数据库）
     * @Date 2024-12-14 22:54
     * @Param file
     **/
/*    @Override
    @Transactional
    public void uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new LFBusinessException("上传文件内容为空");
        }
        String fileName = file.getOriginalFilename();
        ifXmlOrXmls(fileName);

        String path = uploadFileToLocal(fileName,file);
        List<Dict> dictList = readLocalExcel(path);
        List<Dict> processedDictList = ifDicetId(dictList);
        upDBExcel(processedDictList);
//        clearLocalFile(path);
    }*/

    /**
     * @return
     * @Author liuchuanping
     * @Description
     * 文件检查
     *  1.先比较文件大小
     *
     *  2.比较文件字节
     *
     *  3.删除文件内容
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

      /*  if (!compareDictLists(dictPoToExcelList, dictPoToDB)) {
            throw new LFBusinessException("数据文件内容与Excel文件内容不一样");
        }*/

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
//            dictMapper.saveDict(dict);
            saveOrUpdate(dict);
        }
        return absolutePath;
    }

    /**********************************private**********************************/


    private boolean compareDictLists(List<DictPoToExcel> excelDicts, List<DictPoToExcel> dbDicts) {
        Set<DictPoToExcel> excelDictSet = new HashSet<>(excelDicts);

        for (DictPoToExcel dict : dbDicts) {
            if (!excelDictSet.contains(dict)) {
                return false;
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
     * 把数据写入数据库
     *
     * @param processedDictList
     */
    private void upDBExcel(List<Dict> processedDictList) {
        if (!saveBatch(processedDictList)){
            throw new LFBusinessException("存入数据库失败");
        }
    }

    /**
     * 判断是否为空
     * 遍历提取ditc_id，如果为空自定义补充
     * 遍历数据库字段ditc_id与excel中ditc_id进行比较，去除重复的值
     * //TODO 如果excel中存在重复主键怎么处理？
     *
     * @param dictList
     */
    private List<Dict> ifDicetId(List<Dict> dictList) {
        if (dictList == null || dictList.isEmpty()) {
            throw new LFBusinessException("读取字典为空");
        }
        List<Integer> DBDictIds = dictMapper.findDBDictIds();  // 数据库中数据

        // 存储处理后的dictList
        List<Dict> processedDictList = new ArrayList<>();

        Iterator<Dict> iterator = dictList.iterator(); // Excel中数据
        while (iterator.hasNext()) {
            Dict dict = iterator.next();
            Integer dictId = dict.getDictId();
            if (dictId == null) {
                String generatedDictId = generateDictId();
                dict.setDictId(Integer.valueOf(generatedDictId));
            } else if (DBDictIds.contains(dictId)) {
                // 如果数据库中已存在该dict_id，从迭代器中移除
                iterator.remove();
                continue;
            }
            // 将处理后的Dict对象添加到新的列表中
            processedDictList.add(dict);
        }

        return processedDictList; // 返回处理后的列表
    }

    /**
     * 根据系统时间生成一个不会重复的6位id
     *
     * @return 6位数字Id
     */
    private String generateDictId() {
        Set<String> set = new HashSet<>();
        Integer result = null;
        synchronized (this) {
            while (set.size() < 6) {
                String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);  // 取UUID的前6个字符
                if (!set.contains(uuid)) {
                    set.add(uuid);
                    // 将UUID的前6个字符转换为整数
                    result = Integer.parseInt(uuid, 16);  // 将16进制字符串转换为整数
                    break; // 找到唯一ID后退出循环
                }
            }
        }
        return result.toString();
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
     * 判断文件类似是不是xlm，xlsx
     */
    private void ifXmlOrXmls(String fileName) {
        String fileSuffix = FileUtils.getFileSuffix(fileName);
        if (!fileSuffix.equals(DictConstants.XLSX_NAME)
                && !fileSuffix.equals(DictConstants.XLS_NAME)
                && !fileSuffix.equals(DictConstants.XLM_NAME)) {
            throw new LFBusinessException("不支持的文件类型");
        }
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




