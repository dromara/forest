package com.dtflys.forest.http;

import com.dtflys.forest.handler.LifeCycleHandler;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:05
 */
public interface ForestResponseFactory<R> {

    ForestResponse createResponse(ForestRequest request, R res, LifeCycleHandler lifeCycleHandler, Throwable exception);

}
