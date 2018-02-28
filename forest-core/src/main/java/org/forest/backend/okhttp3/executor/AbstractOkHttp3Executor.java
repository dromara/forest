package org.forest.backend.okhttp3.executor;

import okhttp3.*;
import org.apache.http.concurrent.FutureCallback;
import org.forest.backend.BodyBuilder;
import org.forest.backend.HttpExecutor;
import org.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import org.forest.backend.okhttp3.response.OkHttp3ForestResponseFactory;
import org.forest.backend.okhttp3.response.OkHttp3ResponseFuture;
import org.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import org.forest.backend.url.URLBuilder;
import org.forest.converter.json.ForestJsonConverter;
import org.forest.exceptions.ForestNetworkException;
import org.forest.handler.ResponseHandler;
import org.forest.http.ForestRequest;
import org.forest.http.ForestResponse;
import org.forest.mapping.MappingTemplate;
import org.forest.utils.RequestNameValue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:55
 */
public abstract class AbstractOkHttp3Executor implements HttpExecutor {

    protected final ForestRequest request;

    private final OkHttp3ConnectionManager connectionManager;

    private final OkHttp3ResponseHandler okHttp3ResponseHandler;


    protected AbstractOkHttp3Executor(ForestRequest request, OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler) {
        this.request = request;
        this.connectionManager = connectionManager;
        this.okHttp3ResponseHandler = okHttp3ResponseHandler;
    }

    protected abstract BodyBuilder<Request.Builder> getBodyBuilder();

    protected abstract URLBuilder getURLBuilder();

    protected OkHttpClient getClient(ForestRequest request) {
        return connectionManager.getClient(request);
    }

    protected void prepareHeaders(Request.Builder builder) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                builder.addHeader(nameValue.getName(), MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
            }
        }
    }

    protected void prepareBody(Request.Builder builder) {
        getBodyBuilder().buildBody(builder, request);
    }

    protected void requestMethod(Request.Builder builder) {
        prepareBody(builder);
    }



    @Override
    public void execute(final ResponseHandler responseHandler) {
        OkHttpClient okHttpClient = getClient(request);
        URLBuilder urlBuilder = getURLBuilder();
        String url = urlBuilder.buildUrl(request);
        Request.Builder builder = new Request.Builder().url(url);
        prepareHeaders(builder);
        requestMethod(builder);

        final Request okRequest = builder.build();
        Call call = okHttpClient.newCall(okRequest);
        final OkHttp3ForestResponseFactory factory = new OkHttp3ForestResponseFactory();

        if (request.isAsync()) {
            final OkHttp3ResponseFuture future = new OkHttp3ResponseFuture();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.failed(e);
                    ForestResponse response = factory.createResponse(request, null);
                    responseHandler.handleError(request, response, e);
                }

                @Override
                public void onResponse(Call call, Response okResponse) throws IOException {
                    ForestResponse response = factory.createResponse(request, okResponse);
                    Object result = null;
                    if (response.isSuccess()) {
                        if (request.getOnSuccess() != null) {
                            result = okHttp3ResponseHandler.handleSuccess(response);
                        }
                        else {
                            result = okHttp3ResponseHandler.handleSync(okResponse, response);
                        }
                        future.completed(result);
                    } else {
                        future.failed(new ForestNetworkException(okResponse.message(), okResponse.code(), response));
                        okHttp3ResponseHandler.handleError(response);
                    }
                }
            });
            okHttp3ResponseHandler.handleFuture(future, factory);
        }
        else {
            Response okResponse = null;
            try {
                okResponse = call.execute();
            } catch (IOException e) {
                responseHandler.handleError(request, null, e);
                return;
            }
            ForestResponse response = factory.createResponse(request, okResponse);
            okHttp3ResponseHandler.handleSync(okResponse, response);
        }
    }

    @Override
    public void close() {
    }
}
