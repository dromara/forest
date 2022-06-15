package com.dtflys.forest.backend;

import com.dtflys.forest.http.ForestRequest;

public interface BackendClientFactory<T> {

    T getClient(ForestRequest request);

}
