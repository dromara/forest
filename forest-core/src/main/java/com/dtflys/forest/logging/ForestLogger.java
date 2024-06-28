package com.dtflys.forest.logging;


import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Forest日志控制对象
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-16 10:16
 */
public interface ForestLogger {

    static ForestLogger getLogger(Class<?> clazz) {
        boolean supportSlf4j = false;
        boolean supportAndroid = false;
        try {
            Class.forName("org.slf4j.Logger");
            supportSlf4j = true;
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("android.util.Log");
                supportAndroid = true;
            } catch (ClassNotFoundException e2) {
            }
        }

        if (supportSlf4j) {
            Class<?> slfLoggerClass = ForestSlf4jLogger.class;
            try {
                Constructor<?> constructor = slfLoggerClass.getConstructor(Class.class);
                return (ForestLogger) constructor.newInstance(clazz);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new ForestRuntimeException(e);
            }
        }

        if (supportAndroid) {
            return new ForestAndroidLogger(clazz);
        }
        return new ForestJDKLogger(clazz);
    }


    /**
     * 输出INFO级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    void info(String content, Object ...args);

    /**
     * 输出ERROR级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    void error(String content, Object ...args);
}
