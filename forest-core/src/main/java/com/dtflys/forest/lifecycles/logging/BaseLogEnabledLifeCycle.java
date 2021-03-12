package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

public class BaseLogEnabledLifeCycle implements BaseAnnotationLifeCycle<LogEnabled, Object> {

    @Override
    public void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, LogEnabled annotation) {
        LogConfiguration logConfiguration = interfaceProxyHandler.getBaseLogConfiguration();
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration();
            interfaceProxyHandler.setBaseLogConfiguration(logConfiguration);
        }
        boolean logEnabled = annotation.value();
        boolean logRequest = annotation.logRequest();
        boolean logResponseStatus = annotation.logResponseStatus();
        boolean logResponseContent = annotation.logResponseContent();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseContent(logResponseContent);
    }

}
