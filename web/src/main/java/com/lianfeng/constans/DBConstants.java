package com.lianfeng.constans;

/**
 * @version 1.8
 * @注释  关于数据库常用常量
 * @Author liuchuanping
 * @Date 2024-12-14 22:56
 */
public interface DBConstants {

    public static final String SHOW_TABLE = "SHOW DATABASES;";
    /**
     * 新增
     */
    public static final String OPERTION_INSERT = "INSERT";

    /**
     * 更新
     */
    public static final String OPERTION_UPDATE = "UPDATE";

    /**
     * 删除
     */
    public static final String OPERTION_DELETE = "DELETE";

    /**
     * 查询
     */
    public static final String OPERTION_SELECT = "SELECT";

    /**
     * 查询总数
     */
    public static final String OPERTION_QUERY_COUNT = "QUERY_COUNT";

    /**
     * 查询最近记录点
     * <p>例如：SELECT MAX(MY_TEST.LAST_TIME) FROM MY_TEST</p>
     */
    public static final String OPERTION_QUERY_MAX = "QUERY_MAX";

    /**
     * 查询表达式and
     */
    public static final String OPERTION_QUERY_AND = "and";

    /**
     * 查询表达式or
     */
    public static final String OPERTION_QUERY_OR = "or";

    /**
     * *
     */
    public static final String FILE_ALL = "*";

    /**
     * FROM
     */
    public static final String OPERTION_FROM = "FROM";

    /**
     * 空格
     */
    public static final String OPERTION_SPACING = " ";

    /**
     * where
     */
    public static final String OPERTION_WHERE = "WHERE";

}
