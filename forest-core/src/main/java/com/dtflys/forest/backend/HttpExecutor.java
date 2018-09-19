package com.dtflys.forest.backend;

import com.dtflys.forest.handler.ResponseHandler;

/**
 * HTTP执行器
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:33
 */
public interface HttpExecutor {

    void execute(ResponseHandler responseHandler);

    void close();
}
