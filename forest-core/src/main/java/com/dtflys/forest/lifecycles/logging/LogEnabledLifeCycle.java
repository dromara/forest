package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

public class LogEnabledLifeCycle implements MethodAnnotationLifeCycle<LogEnabled, Void> {

    @Override
    public void onMethodInitialized(ForestMethod method, LogEnabled annotation) {
        MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            return;
        }
        LogConfiguration logConfiguration = metaRequest.getLogConfiguration();
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration();
            metaRequest.setLogConfiguration(logConfiguration);
        }
        boolean logEnabled = annotation.value();
        boolean logRequest = annotation.logRequest();
        boolean logRequestHeaders = annotation.logRequestHeaders();
        boolean logRequestBody = annotation.logRequestBody();
        boolean logResponseStatus = annotation.logResponseStatus();
        boolean logResponseHeaders = annotation.logResponseHeaders();
        boolean logResponseContent = annotation.logResponseContent();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogRequestHeaders(logRequestHeaders);
        logConfiguration.setLogRequestBody(logRequestBody);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseHeaders(logResponseHeaders);
        logConfiguration.setLogResponseContent(logResponseContent);
        metaRequest.setLogConfiguration(logConfiguration);
    }
}
