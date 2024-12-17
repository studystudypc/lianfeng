package com.lianfeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lianfeng.model.entity.Dict;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

/**
* @author LCP
* @description 针对表【dict】的数据库操作Mapper
* @createDate 2024-12-12 21:30:58
* @Entity .com.lianfeng.model.entity.Dict
*/
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

}




