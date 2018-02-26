package org.forest.backend;

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

    ResultHandler getDefaultResultHandler();

    HttpExecutor createExecutor(ForestRequest request, ResponseHandler responseHandler);

}
