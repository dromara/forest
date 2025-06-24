package com.dtflys.forest.http;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.utils.ReflectUtils;

import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-12 17:05
 */
public interface ForestResponseFactory<R> {

    default boolean isUnclosedResponse(ForestRequest<?> request, LifeCycleHandler lifeCycleHandler) {
        final Class<?> clazz = ReflectUtils.toClass(lifeCycleHandler.getResultType());
        if (clazz != null && UnclosedResponse.class.isAssignableFrom(clazz)) {
            return true;
        }
        if (request.isReceiveStream()) {
            return true;
        }
        return false;
    }

    ForestResponse<?> createResponse(ForestRequest<?> request, R res, LifeCycleHandler lifeCycleHandler, Throwable exception, Date requestTime);

}
