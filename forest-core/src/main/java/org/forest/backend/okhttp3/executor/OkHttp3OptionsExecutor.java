package org.forest.backend.okhttp3.executor;

import okhttp3.Request;
import org.forest.backend.BodyBuilder;
import org.forest.backend.NoneBodyBuilder;
import org.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import org.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import org.forest.backend.url.SimpleURLBuilder;
import org.forest.backend.url.URLBuilder;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:12
 */
public class OkHttp3OptionsExecutor extends AbstractOkHttp3Executor {

    private static final BodyBuilder bodyBuilder = new NoneBodyBuilder();
    private static final URLBuilder urlBuilder = new SimpleURLBuilder();

    public OkHttp3OptionsExecutor(OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler, ForestRequest request) {
        super(request, connectionManager, okHttp3ResponseHandler);
    }

    @Override
    protected void requestMethod(Request.Builder builder) {
        builder.method("OPTIONS", null);
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
