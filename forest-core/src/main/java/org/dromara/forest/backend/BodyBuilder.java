package org.dromara.forest.backend;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 14:50
 */
public interface BodyBuilder<R> {

    void buildBody(R req, ForestRequest request, LifeCycleHandler lifeCycleHandler);
}
