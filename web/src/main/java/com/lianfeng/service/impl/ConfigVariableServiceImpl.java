package com.lianfeng.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.ConfigVariableMapper;
import com.lianfeng.model.entity.ConfigVariable;
import com.lianfeng.service.IConfigVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【config_variable(配置变量表，用于存储配置相关变量信息)】的数据库操作Service实现
* @createDate 2025-01-25 13:25:09
*/
@Service
public class ConfigVariableServiceImpl extends ServiceImpl<ConfigVariableMapper, ConfigVariable>
    implements IConfigVariableService {


}




