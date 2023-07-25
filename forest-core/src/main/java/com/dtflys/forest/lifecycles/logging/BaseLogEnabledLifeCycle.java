package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.lifecycles.BaseAnnotationLifeCycle;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.proxy.InterfaceProxyHandler;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

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
