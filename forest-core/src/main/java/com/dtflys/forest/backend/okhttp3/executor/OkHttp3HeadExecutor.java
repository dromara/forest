package com.dtflys.forest.backend.okhttp3.executor;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.body.NoneBodyBuilder;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import okhttp3.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:12
 */
public class OkHttp3HeadExecutor extends AbstractOkHttp3Executor {

    private static final BodyBuilder bodyBuilder = new NoneBodyBuilder();

    public OkHttp3HeadExecutor(OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler, ForestRequest request) {
        super(request, connectionManager, okHttp3ResponseHandler);
    }

    @Override
    protected void requestMethod(Request.Builder builder) {
        builder.head();
    }

    @Override
    protected BodyBuilder<Request.Builder> getBodyBuilder() {
        return bodyBuilder;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return URLBuilder.getQueryableURLBuilder();
    }
}
