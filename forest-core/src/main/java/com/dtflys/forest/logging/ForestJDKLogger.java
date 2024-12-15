package com.dtflys.forest.logging;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class ForestJDKLogger implements ForestLogger {

    private final Logger logger;


    public ForestJDKLogger(Class<?> clazz) {
        logger = Logger.getLogger(clazz.getName());
    }

    @Override
    public void info(String content, Object... args) {
        if (args == null || args.length == 0) {
            logger.info(content);
        } else {
            logger.info(MessageFormat.format(content, args));
        }
    }

    @Override
    public void error(String content, Object... args) {
        if (args == null || args.length == 0) {
            logger.severe(content);
        } else {
            logger.severe(MessageFormat.format(content, args));
        }
    }

}
