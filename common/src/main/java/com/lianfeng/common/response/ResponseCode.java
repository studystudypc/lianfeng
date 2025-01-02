package com.lianfeng.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 项目公用返回状态码
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {

    /**
     * 成功
     */
    SUCCESS(200, "SUCCESS"),
    /**
     * 错误
     */
    ERROR(99, "ERROR"),
    /**
     * token过期
     */
    TOKEN_EXPIRE(2, "TOKEN_EXPIRE"),
    /**
     * 参数错误
     */
    ERROR_PARAM(3, "ERROR_PARAM"),
    /**
     * 无权限访问
     */
    ACCESS_DENIED(4, "ACCESS_DENIED"),

    /**
     * 请求无效，客户端传递的参数错误或缺少必要参数
     */
    BAD_REQUEST(400, "Bad Request"),

    FILE_UPLOAD_EMPTY(205,"文件上传为空或文件名字为空"),
    PRIMARY_KEY_NOT_FOUND(206,"指定的主键未找到"),
    INVALID_FIELD_FORMAT(207,"字段格式错误，必须为 key=value 格式"),
    FREQUENT_CLICK(208,"点击请勿频繁，请稍等重试"),
    FILE_UPLOAD_FAILED(209,"文件上传失败"),
    TABLE_NAME_CANNOT_BE_NULL(210, "表名不能为空"),

    DATABASE_CONNECTION_INSUFFICIENT(100, "数据库连接信息不足"),
    QUERY_TABLE_STRUCTURE_ERROR(101, "查询表结构时发生错误"),
    DATABASE_TABLE_NOT_EXIST(102, "数据库表不存在"),
    FIELD_COUNT_MISMATCH(103, "源表和目标表的字段数量不一致"),
    TABLE_STRUCTURE_DIFFERENT(104, "表结构不相同"),
    LOG_FILE_DIRECTORY_CREATION_FAILED(104, "无法创建日志文件目录");

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String desc;

}
