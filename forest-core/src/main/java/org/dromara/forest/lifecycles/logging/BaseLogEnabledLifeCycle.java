package org.dromara.forest.lifecycles.logging;

import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.lifecycles.BaseAnnotationLifeCycle;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.proxy.InterfaceProxyHandler;

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
