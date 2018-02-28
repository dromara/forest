package org.forest.backend.okhttp3;

import org.forest.backend.AbstractHttpBackend;
import org.forest.backend.ForestConnectionManager;
import org.forest.backend.HttpExecutor;
import org.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import org.forest.backend.okhttp3.executor.*;
import org.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import org.forest.config.ForestConfiguration;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-26 19:09
 */
public class OkHttp3Backend extends AbstractHttpBackend {


    @Override
    public String getName() {
        return "okhttp3";
    }

    public OkHttp3Backend() {
        super(new OkHttp3ConnectionManager());
    }

    @Override
    protected HttpExecutor createHeadExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3HeadExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);
    }

    @Override
    protected HttpExecutor createGetExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3GetExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);

    }

    @Override
    protected HttpExecutor createPostExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3PostExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);
    }

    @Override
    protected HttpExecutor createPutExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
            return new OkHttp3PutExecutor(
                    (OkHttp3ConnectionManager) connectionManager,
                    getOkHttp3ResponseHandler(request, responseHandler),
                    request);

        }

    @Override
    protected HttpExecutor createDeleteExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3DeleteExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);

    }

    @Override
    protected HttpExecutor createOptionsExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3OptionsExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);

    }

    @Override
    protected HttpExecutor createTraceExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3TraceExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);

    }

    @Override
    protected HttpExecutor createPatchExecutor(ForestConnectionManager connectionManager, ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3PatchExecutor(
                (OkHttp3ConnectionManager) connectionManager,
                getOkHttp3ResponseHandler(request, responseHandler),
                request);
    }


    private OkHttp3ResponseHandler getOkHttp3ResponseHandler(ForestRequest request, ResponseHandler responseHandler) {
        return new OkHttp3ResponseHandler(request, responseHandler);
    }
}
