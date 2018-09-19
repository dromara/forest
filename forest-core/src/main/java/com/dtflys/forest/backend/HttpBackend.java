package com.dtflys.forest.backend;

import com.dtflys.forest.config.ForestConfiguration;
import com.dtflys.forest.handler.ResponseHandler;
import com.dtflys.forest.http.ForestRequest;

/**
 * HTTP后端接口
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-04-20 18:22
 */
public interface HttpBackend {

    String getName();

    HttpExecutor createExecutor(ForestRequest request, ResponseHandler responseHandler);

    void init(ForestConfiguration configuration);

    interface HttpExecutorCreator {
        HttpExecutor createExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler);
    }

}
