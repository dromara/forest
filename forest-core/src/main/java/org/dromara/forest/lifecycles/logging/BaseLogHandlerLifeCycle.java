package org.dromara.forest.lifecycles.logging;

import org.dromara.forest.annotation.LogHandler;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.lifecycles.BaseAnnotationLifeCycle;
import org.dromara.forest.logging.ForestLogHandler;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.proxy.InterfaceProxyHandler;

public class BaseLogHandlerLifeCycle implements BaseAnnotationLifeCycle<LogHandler, Object> {


    @Override
    public void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, LogHandler annotation) {
        ForestConfiguration configuration = interfaceProxyHandler.getConfiguration();
        LogConfiguration logConfiguration = interfaceProxyHandler.getBaseLogConfiguration();
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration();
            logConfiguration.setLogEnabled(configuration.isLogEnabled());
            logConfiguration.setLogRequest(configuration.isLogRequest());
            logConfiguration.setLogResponseStatus(configuration.isLogResponseStatus());
            logConfiguration.setLogResponseContent(configuration.isLogResponseContent());
            interfaceProxyHandler.setBaseLogConfiguration(logConfiguration);
        }
        Class<? extends ForestLogHandler> logHandlerClass = annotation.value();
        ForestLogHandler logHandler = null;
        try {
            logHandler = logHandlerClass.newInstance();
        } catch (InstantiationException e) {
            throw new ForestRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new ForestRuntimeException(e);
        }
        if (logHandler != null) {
            logConfiguration.setLogHandler(logHandler);
        } else {
            logConfiguration.setLogHandler(configuration.getLogHandler());
        }
    }

}
