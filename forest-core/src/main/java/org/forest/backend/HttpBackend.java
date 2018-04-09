package org.forest.backend;

import org.forest.backend.httpclient.conn.HttpclientConnectionManager;
import org.forest.config.ForestConfiguration;
import org.forest.handler.ResponseHandler;
import org.forest.handler.ResultHandler;
import org.forest.http.ForestRequest;

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
