package org.forest.backend;

import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;

/**
 * HTTP执行器
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:33
 */
public interface HttpExecutor {

    ForestRequest getRequest();

    ForestResponse getResponse();

    void execute(ResponseHandler responseHandler);

    void close();
}
