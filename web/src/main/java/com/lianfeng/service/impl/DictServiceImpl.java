package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.common.listenner.ExcelListenner;
import com.lianfeng.common.utils.ExcelUtils;
import com.lianfeng.common.utils.FileUtils;
import com.lianfeng.constans.DictConstants;
import com.lianfeng.model.entity.Dict;
import com.lianfeng.mapper.DictMapper;
import com.lianfeng.service.IDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Override
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
    }

    /**
     * @return
     * @Author liuchuanping
     * @Description 文件检查
     *  1.先比较文件大小
     *
     *  （思路1）
     *  查出数据库内容，转换为json字符
     *  在读出excel文件，转换为json字符
     *  比较json字符串
     *
     *  （思路2）
     *  查出数据库直接与excle进行比较
     * @Date 2024-12-15 22:44
     * @param file
     **/
    @Override
    public void checkFile(MultipartFile file) {

    }

    /**********************************private**********************************/

    /**
     * 删除Excel文件
     */
    private void clearLocalFile(String path) {
        try {
            Path filePath = Paths.get(path);
            Files.delete(filePath);
        } catch (Exception e) {
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
                dict.setDictId(generateDictId());
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
    private Integer generateDictId() {
        Set<String> set = new HashSet<>();
        Integer result = null;
        while (set.size() < 6) {
            String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6);  // 取UUID的前6个字符
            if (!set.contains(uuid)) {
                set.add(uuid);
                // 将UUID的前6个字符转换为整数
                result = Integer.parseInt(uuid, 16);  // 将16进制字符串转换为整数
            }
        }
        return result;
    }

    /**
     * 读Excel文件
     *
     * @param path
     */
    private List<Dict> readLocalExcel(String path) {
        ExcelListenner<Dict> listener = new ExcelListenner<>();
        ExcelUtils.readExcel(path, Dict.class, listener);
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
     *
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




