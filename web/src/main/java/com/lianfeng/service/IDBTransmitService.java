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
}
