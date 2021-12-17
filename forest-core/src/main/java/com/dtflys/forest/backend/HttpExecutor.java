package com.dtflys.forest.backend;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;

/**
 * HTTP执行器
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:33
 */
public interface HttpExecutor {

    ForestRequest getRequest();

    void execute(LifeCycleHandler lifeCycleHandler);

    ResponseHandler getResponseHandler();

    void close();
}
