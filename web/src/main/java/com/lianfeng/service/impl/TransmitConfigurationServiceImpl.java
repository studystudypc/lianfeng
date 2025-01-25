package com.lianfeng.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.TransmitConfigurationMapper;
import com.lianfeng.model.entity.TransmitConfiguration;
import com.lianfeng.service.ITransmitConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【transmit_configuration(配置表，用于存储配置信息)】的数据库操作Service实现
* @createDate 2025-01-25 13:25:09
*/
@Service
public class TransmitConfigurationServiceImpl extends ServiceImpl<TransmitConfigurationMapper, TransmitConfiguration>
    implements ITransmitConfigurationService {

}




