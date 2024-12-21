package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.DbConnectionInfoMapper;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.service.IDbConnectionInfoService;
import com.lianfeng.vo.DbConnectionInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【db_connection_info(数据库连接信息表)】的数据库操作Service实现
* @createDate 2024-12-21 09:54:57
*/
@Service
public class DbConnectionInfoServiceImpl extends ServiceImpl<DbConnectionInfoMapper, DbConnectionInfo>
    implements IDbConnectionInfoService {

    @Autowired
    private DbConnectionInfoMapper dbConnectionInfoMapper;

    //保存或更新数据库源信息
    @Override
    public void saveOrUpdateApartment(DbConnectionInfoVo dbConnectionInfoVo) {
        DbConnectionInfo dbConnectionInfo = new DbConnectionInfo();
        BeanUtils.copyProperties(dbConnectionInfo,dbConnectionInfoVo);
        super.saveOrUpdate(dbConnectionInfo);
    }
}




