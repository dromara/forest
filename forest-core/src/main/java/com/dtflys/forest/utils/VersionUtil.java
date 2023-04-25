package com.dtflys.forest.utils;

import com.dtflys.forest.Forest;

import java.util.Optional;

/**
 * Forest 版本号工具类
 *
 * @author gongjun[dt_flys@hotmail.com]
 * @since 1.5.31
 */
public abstract class VersionUtil {

    /**
     * 获取当前 Forest 版本号
     *
     * @return 当前 Forest 版本号
     * @since 1.5.31
     */
    public static String getForestVersion() {
        return Optional.ofNullable(VersionUtil.class.getPackage()).map(Package::getImplementationVersion).orElse("dev");
    }
}
