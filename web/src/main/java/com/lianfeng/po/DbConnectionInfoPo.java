package com.lianfeng.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据库连接信息表
 * @TableName db_connection_info
 */
@TableName(value ="db_connection_info")
@Data
public class DbConnectionInfoPo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String dbUrl;

    /**
     * 
     */
    private String dbUsername;

    /**
     * 
     */
    private String dbPassword;

    /**
     * 
     */
    private String dbDriver;

    /**
     * 记录创建时间
     */
    private Date createdAt;

    /**
     * 记录更新时间，更新记录时自动设置为当前时间
     */
    private Date updatedAt;

    /**
     * 逻辑删除标志，1表示已删除，0表示未删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DbConnectionInfoPo other = (DbConnectionInfoPo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDbUrl() == null ? other.getDbUrl() == null : this.getDbUrl().equals(other.getDbUrl()))
            && (this.getDbUsername() == null ? other.getDbUsername() == null : this.getDbUsername().equals(other.getDbUsername()))
            && (this.getDbPassword() == null ? other.getDbPassword() == null : this.getDbPassword().equals(other.getDbPassword()))
            && (this.getDbDriver() == null ? other.getDbDriver() == null : this.getDbDriver().equals(other.getDbDriver()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDbUrl() == null) ? 0 : getDbUrl().hashCode());
        result = prime * result + ((getDbUsername() == null) ? 0 : getDbUsername().hashCode());
        result = prime * result + ((getDbPassword() == null) ? 0 : getDbPassword().hashCode());
        result = prime * result + ((getDbDriver() == null) ? 0 : getDbDriver().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dbUrl=").append(dbUrl);
        sb.append(", dbUsername=").append(dbUsername);
        sb.append(", dbPassword=").append(dbPassword);
        sb.append(", dbDriver=").append(dbDriver);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}