package org.forest.backend.okhttp3.executor;

import okhttp3.Request;
import org.forest.backend.BodyBuilder;
import org.forest.backend.okhttp3.body.OkHttp3PutBodyBuilder;
import org.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import org.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import org.forest.backend.url.SimpleURLBuilder;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:12
 */
public class OkHttp3PutExecutor extends AbstractOkHttp3Executor {

    private static final BodyBuilder bodyBuilder = new OkHttp3PutBodyBuilder();
    private static final URLBuilder urlBuilder = new SimpleURLBuilder();

    public OkHttp3PutExecutor(OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler, ForestRequest request) {
        super(request, connectionManager, okHttp3ResponseHandler);
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
