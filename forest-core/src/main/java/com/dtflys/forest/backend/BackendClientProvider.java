package com.dtflys.forest.backend;

import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;

public interface BackendClientProvider<T> {

    T getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler);

}
