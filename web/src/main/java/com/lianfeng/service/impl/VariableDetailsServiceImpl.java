package com.lianfeng.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.VariableDetailsMapper;
import com.lianfeng.model.entity.VariableDetails;
import com.lianfeng.service.IVariableDetailsService;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【variable_details(变量明细表，用于存储每个变量的详细数据)】的数据库操作Service实现
* @createDate 2025-01-25 13:25:08
*/
@Service
public class VariableDetailsServiceImpl extends ServiceImpl<VariableDetailsMapper, VariableDetails>
    implements IVariableDetailsService {

}




