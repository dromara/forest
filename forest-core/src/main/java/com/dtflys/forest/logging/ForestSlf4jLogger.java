package com.dtflys.forest.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForestSlf4jLogger implements ForestLogger {

    private final Logger logger;

    public ForestSlf4jLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }


    /**
     * 输出INFO级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    @Override
    public void info(String content, Object ...args) {
        logger.info(content, args);
    }

    /**
     * 输出ERROR级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    @Override
    public void error(String content, Object ...args) {
        logger.error(content, args);
    }

}
