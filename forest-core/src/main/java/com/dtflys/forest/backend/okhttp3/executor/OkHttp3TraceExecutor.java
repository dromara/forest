package com.dtflys.forest.backend.okhttp3.executor;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.NoneBodyBuilder;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.backend.url.QueryableURLBuilder;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import okhttp3.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:12
 */
public class OkHttp3TraceExecutor extends AbstractOkHttp3Executor {

    private static final BodyBuilder bodyBuilder = new NoneBodyBuilder();
    private static final URLBuilder urlBuilder = new QueryableURLBuilder();

    public OkHttp3TraceExecutor(OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler, ForestRequest request) {
        super(request, connectionManager, okHttp3ResponseHandler);
    }

    @Override
    protected void requestMethod(Request.Builder builder) {
        builder.method("TRACE", null);
    }

    @Override
    protected BodyBuilder<Request.Builder> getBodyBuilder() {
        return bodyBuilder;
    }

    @Override
    protected URLBuilder getURLBuilder() {
        return urlBuilder;
    }
}
