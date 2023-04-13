package org.dromara.forest.lifecycles.logging;

import org.dromara.forest.annotation.LogHandler;
import org.dromara.forest.config.ForestConfiguration;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.logging.ForestLogHandler;
import org.dromara.forest.logging.LogConfiguration;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;

public class LogHandlerLifeCycle implements MethodAnnotationLifeCycle<LogHandler, Object> {


    @Override
    public void onMethodInitialized(ForestMethod method, LogHandler annotation) {
        MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            return;
        }
        ForestConfiguration configuration = method.getConfiguration();
        LogConfiguration logConfiguration = metaRequest.getLogConfiguration();
        if (logConfiguration == null) {
            logConfiguration = new LogConfiguration();
            logConfiguration.setLogEnabled(configuration.isLogEnabled());
            logConfiguration.setLogRequest(configuration.isLogRequest());
            logConfiguration.setLogResponseStatus(configuration.isLogResponseStatus());
            logConfiguration.setLogResponseContent(configuration.isLogResponseContent());
            metaRequest.setLogConfiguration(logConfiguration);
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
