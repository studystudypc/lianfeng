package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.DbConnectionInfoMapper;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.service.IDbConnectionInfoService;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【db_connection_info(数据库连接信息表)】的数据库操作Service实现
* @createDate 2024-12-21 09:54:57
*/
@Service
public class DbConnectionInfoServiceImpl extends ServiceImpl<DbConnectionInfoMapper, DbConnectionInfo>
    implements IDbConnectionInfoService {

}




