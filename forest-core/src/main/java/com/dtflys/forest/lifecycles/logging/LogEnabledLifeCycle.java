package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

public class LogEnabledLifeCycle implements MethodAnnotationLifeCycle<LogEnabled> {

    @Override
    public void onMethodInitialized(ForestMethod method, LogEnabled annotation) {
        MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            return;
        }
        ForestConfiguration configuration = method.getConfiguration();
        LogConfiguration logConfiguration = metaRequest.getLogConfiguration();
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration();
            metaRequest.setLogConfiguration(logConfiguration);
        }
        boolean logEnabled = annotation.value();
        boolean logRequest = annotation.logRequest();
        boolean logResponseStatus = annotation.logResponseStatus();
        boolean logResponseContent = annotation.logResponseContent();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseContent(logResponseContent);
        metaRequest.setLogConfiguration(logConfiguration);
    }
}
