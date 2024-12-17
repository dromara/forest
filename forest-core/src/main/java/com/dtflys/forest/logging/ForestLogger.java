package com.dtflys.forest.logging;


import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.util.PrimitiveIterator;

/**
 * Forest日志控制对象
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-16 10:16
 */
public interface ForestLogger {
    
    static ForestLogger getLogger(Class<?> clazz) {
        boolean supportSlf4j = false;
        boolean supportAndroid = ReflectUtils.isAndroid();

        if (supportAndroid) {
            return createLogger("com.dtflys.forest.logging.ForestAndroidLogger", clazz);
        }

        try {
            Class.forName("org.slf4j.Logger");
            supportSlf4j = true;
        } catch (ClassNotFoundException e2) {
        }

        if (supportSlf4j) {
            return createLogger("com.dtflys.forest.logging.ForestSlf4jLogger", clazz);
        }

        return new ForestJDKLogger(clazz);
    }


    static ForestLogger createLogger(String className, Class<?> clazz) {
        try {
            Class<?> slfLoggerClass = Class.forName(className);
            Constructor<?> constructor = slfLoggerClass.getConstructor(Class.class);
            return (ForestLogger) constructor.newInstance(clazz);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
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
