package com.lianfeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface DatabaseMapper extends BaseMapper<Object> {

    /**
     * 动态插入数据到指定表
     *
     * @param tableName 表名
     * @param dataMap 数据映射
     */
    @Insert("<script>INSERT INTO ${tableName} <choose>" +
            "<when test='dataMap != null and dataMap.size() > 0'>" +
            "(<foreach collection='dataMap.keys' item='key' separator=','> ${key} </foreach>) VALUES " +
            "(<foreach collection='dataMap.values' item='value' separator=','> #{value} </foreach>)" +
            "</when></choose></script>")
    void insertData(String tableName, Map<String, Object> dataMap);
}