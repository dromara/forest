package org.dromara.forest.backend;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponseFactory;

/**
 * HTTP执行器
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:33
 */
public interface HttpExecutor {

    ForestRequest getRequest();

    void execute(LifeCycleHandler lifeCycleHandler);

    ResponseHandler getResponseHandler();

    ForestResponseFactory getResponseFactory();

    void close();
}
