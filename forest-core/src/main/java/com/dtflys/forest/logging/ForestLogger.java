package com.dtflys.forest.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forest日志控制对象
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2020-09-16 10:16
 */
public class ForestLogger {

    private final static Logger logger = LoggerFactory.getLogger(DefaultLogHandler.class);

    /**
     * 输出INFO级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    public void info(String content, Object ...args) {
        logger.info(content, args);
    }

    /**
     * 输出ERROR级别内容到日志
     * @param content 日志内容
     * @param args 参数列表
     */
    public void error(String content, Object ...args) {
        logger.error(content, args);
    }
}
