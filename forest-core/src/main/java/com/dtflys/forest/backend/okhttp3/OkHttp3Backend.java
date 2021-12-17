package com.dtflys.forest.backend.okhttp3;

import com.dtflys.forest.backend.AbstractHttpBackend;
import com.dtflys.forest.backend.ForestConnectionManager;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.executor.*;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:09
 */
public class OkHttp3Backend extends AbstractHttpBackend {

    public final static String NAME = "okhttp3";

    @Override
    public String getName() {
        return NAME;
    }

    public OkHttp3Backend() {
        super(new OkHttp3ConnectionManager());
    }

    @Override
    public HttpExecutor createSyncExecutor(ForestConnectionManager connectionManager, ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new OkHttp3Executor(
                request,
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, lifeCycleHandler));
    }



    private OkHttp3ResponseHandler getOkHttp3ResponseHandler(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return new OkHttp3ResponseHandler(request, lifeCycleHandler);
    }
}
