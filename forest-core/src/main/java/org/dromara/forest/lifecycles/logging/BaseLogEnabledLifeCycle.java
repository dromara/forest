package org.dromara.forest.lifecycles.logging;

import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.lifecycles.BaseAnnotationLifeCycle;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.proxy.InterfaceProxyHandler;

import java.util.Optional;

public class BaseLogEnabledLifeCycle implements BaseAnnotationLifeCycle<LogEnabled, Object> {

    @Override
    public void onProxyHandlerInitialized(InterfaceProxyHandler interfaceProxyHandler, LogEnabled annotation) {
        final LogConfiguration logConfiguration = Optional.ofNullable(interfaceProxyHandler.getBaseLogConfiguration())
                .orElseGet(() -> {
                    final LogConfiguration conf = new LogConfiguration();
                    interfaceProxyHandler.setBaseLogConfiguration(conf);
                    return conf;
                });
        final boolean logEnabled = annotation.value();
        final boolean logRequest = annotation.logRequest();
        final boolean logResponseStatus = annotation.logResponseStatus();
        final boolean logResponseContent = annotation.logResponseContent();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseContent(logResponseContent);
    }

}
