package com.lianfeng.common.utils;

import com.alibaba.excel.util.StringUtils;
import cn.hutool.core.date.DateUtil;
import com.lianfeng.common.constants.LFanConstants;
import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @version 1.8
 * @注释  文件处理工具类
 * @Author liuchuanping
 * @Date 2024-12-14 21:11
 */
public class FileUtils {



    /**
     * 获取文件的后缀
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(LFanConstants.POINT_STR) == LFanConstants.MINUS_ONE_INT) {
            return LFanConstants.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(LFanConstants.POINT_STR)).toLowerCase();
    }

    /**
     * 获取文件的类型
     *
     * @param filename
     * @return
     */
    public static String getFileExtName(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(LFanConstants.POINT_STR) == LFanConstants.MINUS_ONE_INT) {
            return LFanConstants.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(LFanConstants.POINT_STR) + LFanConstants.ONE_INT).toLowerCase();
    }

    /**
     * 批量删除物理文件
     *
     * @param realFilePathList
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if (CollectionUtils.isEmpty(realFilePathList)) {
            return;
        }
        for (String realFilePath : realFilePathList) {
            org.apache.commons.io.FileUtils.forceDelete(new File(realFilePath));
        }
    }

    /**
     * 生成文件的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     *
     * @param basePath
     * @param filename
     * @return
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename))
                .toString();

    }

    /**
     * 创建文件
     * 包含父文件一起视情况去创建
     *
     * @param targetFile
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }

    /**
     * 生成文件分片的存储路径
     * <p>
     * 生成规则：基础路径 + 年 + 月 + 日 + 唯一标识 + 随机的文件名称 + __,__ + 文件分片的下标
     *
     * @param basePath
     * @param identifier
     * @param chunkNumber
     * @return
     */
    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(LFanConstants.COMMON_SEPARATOR)
                .append(chunkNumber)
                .toString();
    }
}
