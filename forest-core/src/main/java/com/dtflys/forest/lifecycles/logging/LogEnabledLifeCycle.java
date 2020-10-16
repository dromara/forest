package com.dtflys.forest.lifecycles.logging;

import com.dtflys.forest.annotation.LogEnabled;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

public class LogEnabledLifeCycle implements MethodAnnotationLifeCycle<LogEnabled, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, LogEnabled annotation) {
        MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            return;
        }
        boolean logEnabled = annotation.value();
        boolean logRequest = annotation.logRequest();
        boolean logResponseStatus = annotation.logResponseStatus();
        boolean logResponseContent = annotation.logResponseContent();
        LogConfiguration logConfiguration = new LogConfiguration();
        logConfiguration.setLogEnabled(logEnabled);
        logConfiguration.setLogRequest(logRequest);
        logConfiguration.setLogResponseStatus(logResponseStatus);
        logConfiguration.setLogResponseContent(logResponseContent);
        metaRequest.setLogConfiguration(logConfiguration);
    }
}
