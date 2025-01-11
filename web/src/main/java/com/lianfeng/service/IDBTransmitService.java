package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianfeng.po.DBTransmitPo;

import java.sql.SQLException;

/**
 * @version 1.8
 * @注释
 * @Author liuchuanping
 * @Date 2024-12-26 10:41
 */
public interface IDBTransmitService extends IService<Object> {

    /**
     * 全表传输
     * @param tableName
     * @param keyName
     * @return
     */
    DBTransmitPo dBTransmit(String tableName, String[] keyName) throws SQLException;

    /**
     * 需要更新的字段
     * @param tableName
     * @param keyName
     * @param fieldName
     * @return
     * @throws SQLException
     */
    DBTransmitPo dBTransmit(String tableName, String[] keyName,String[] fieldName) throws SQLException;

    /**
     * 需要更新的主键
     * @param tableName
     * @param keyName
     * @param keyValue
     * @return
     */
    DBTransmitPo dBTransmitKey(String tableName, String[] keyName, String[] keyValue) throws SQLException;

    /**
     * 全字段更新
     * @param tableName
     * @param keyName
     * @param keyValue
     * @return
     */
    DBTransmitPo dBTransmit(String tableName, String[] keyName, String[] keyValue, String[] fieldName) throws SQLException;

    /**
     * 返回某个表的字段名字
     * @param name
     * @return
     */
    DBTransmitPo reField(String name) throws SQLException;
}
