package com.lianfeng.constans;

/**
 * @version 1.8
 * @注释 Redis的配置
 * @Author liuchuanping
 * @Date 2025-01-04 10:39
 */
public interface RedisConstant {

    /**
     * 数据库中每个表的总数
     */
    public static final String PROGRESS_BAR_ALL = "progress:bar:all:";
    public static final Integer PROGRESS_BAR_ALL_CAPTCHA_TTL_SEC = 60;

    /**
     *
     */
    public static final String PROGRESS_BAR_SINGLE = "progress:bar:single:";
    public static final Integer PROGRESS_BAR_SINGLE_CAPTCHA_TTL_SEC = 60;


}
