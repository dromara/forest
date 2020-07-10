package com.dtflys.forest.backend.okhttp3.executor;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.okhttp3.body.OkHttp3DeleteBodyBuilder;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.backend.url.SimpleURLBuilder;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import okhttp3.Request;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:12
 */
public class OkHttp3DeleteExecutor extends AbstractOkHttp3Executor {

    private static final BodyBuilder bodyBuilder = new OkHttp3DeleteBodyBuilder();

    public OkHttp3DeleteExecutor(OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler, ForestRequest request) {
        super(request, connectionManager, okHttp3ResponseHandler);
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
