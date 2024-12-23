package com.lianfeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lianfeng.mapper.ExampleTableMapper;
import com.lianfeng.model.entity.ExampleTable;
import com.lianfeng.service.IExampleTableService;
import org.springframework.stereotype.Service;

/**
* @author LCP
* @description 针对表【example_table(数据库常见表信息)】的数据库操作Service实现
* @createDate 2024-12-21 13:27:21
*/
@Service
public class ExampleTableServiceImpl extends ServiceImpl<ExampleTableMapper, ExampleTable>
    implements IExampleTableService {

}




