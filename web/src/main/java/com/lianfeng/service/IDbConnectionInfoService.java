package com.lianfeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lianfeng.model.entity.DbConnectionInfo;
import com.lianfeng.po.CompareTablePo;
import com.lianfeng.vo.DBNameVo;
import com.lianfeng.vo.DbConnectionInfoVo;

import java.sql.SQLException;
import java.util.List;

/**
* @author LCP
* @description 针对表【db_connection_info(数据库连接信息表)】的数据库操作Service
* @createDate 2024-12-21 09:54:57
*/
public interface IDbConnectionInfoService extends IService<DbConnectionInfo> {

    /**
     * 保存或更新数据库源信息
     * @param dbConnectionInfoVo
     */
    void saveOrUpdateApartment(DbConnectionInfoVo dbConnectionInfoVo);

    /**
     * 对比表的数据
     * @return
     */
    CompareTablePo compareTable(String table) throws SQLException;

    List<DBNameVo> returnTableName() throws SQLException;
}
