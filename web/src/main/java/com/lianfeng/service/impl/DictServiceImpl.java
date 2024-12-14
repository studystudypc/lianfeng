package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.common.exception.LFBusinessException;
import com.lianfeng.model.entity.Dict;
import com.lianfeng.mapper.DictMapper;
import com.lianfeng.service.IDictService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
* @author LCP
* @description 针对表【dict】的数据库操作Service实现
* @createDate 2024-12-12 21:30:58
*/
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict>
    implements IDictService {

    /**
     * @Author liuchuaning
     * @Description * 文件上传接口，并把Excel数据存入数据库
     *  判断上传文件是否存在
     *  判断文件类似是不是xlm，xlms
     *  上传文件到本地路径（使用系统时间设置名字，不会重复）
     *  读取Excel文件
     *  上传到数据库
     *  删除本地文件（因为业务只是要读取上传的EXcel内容到数据库）
     * @Date 2024-12-14 22:54
     * @Param file
     **/
    @Override
    public void uploadFile(MultipartFile file) {
        if (!file.isEmpty()){
            throw new LFBusinessException("上传文件内容为空");
        }
        ifXmlOrXmls();
        uploadFileToLocal();//可以兼容Linux和Window
        readLocalExcel();
        UpDBExcel();
        ClearLocalFile();
    }
}




