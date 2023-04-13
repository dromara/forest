package org.dromara.forest.lifecycles.logging;

import org.dromara.forest.annotation.LogEnabled;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;

public class LogEnabledLifeCycle implements MethodAnnotationLifeCycle<LogEnabled, Object> {

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
