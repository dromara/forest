package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogHandler;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

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
        ForestLogHandler logHandler = configuration.getForestObject(logHandlerClass);
        if (logHandler != null) {
            logConfiguration.setLogHandler(logHandler);
        } else {
            logConfiguration.setLogHandler(configuration.getLogHandler());
        }
    }

}
