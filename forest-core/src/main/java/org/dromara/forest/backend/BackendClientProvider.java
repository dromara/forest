package org.dromara.forest.backend;

import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.http.ForestRequest;

public interface BackendClientProvider<T> {

    T getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler);

}
