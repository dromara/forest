package com.dtflys.forest.backend.okhttp3.executor;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.backend.okhttp3.logging.OkHttp3LogBodyMessage;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.logging.LogBodyMessage;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.LogHandler;
import com.dtflys.forest.logging.LogHeaderMessage;
import com.dtflys.forest.logging.RequestLogMessage;
import com.dtflys.forest.logging.ResponseLogMessage;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ForestResponseFactory;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseFuture;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.mapping.MappingTemplate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.List;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:55
 */
public abstract class AbstractOkHttp3Executor implements HttpExecutor {

    private final static Logger log = LoggerFactory.getLogger(AbstractOkHttp3Executor.class);

    protected final ForestRequest request;

    private final OkHttp3ConnectionManager connectionManager;

    private final OkHttp3ResponseHandler okHttp3ResponseHandler;

    protected RequestLogMessage buildRequestMessage(int retryCount, Request okRequest) {
        RequestLogMessage message = new RequestLogMessage();
        HttpUrl url = okRequest.url();
        String scheme = url.scheme().toUpperCase();
        String uri = url.uri().toString();
        String method = okRequest.method();
        message.setUri(uri);
        message.setType(method);
        message.setScheme(scheme);
        message.setRetryCount(retryCount);
        setLogHeaders(message, okRequest);
        setLogBody(message, okRequest);
        return message;
    }

    protected void setLogHeaders(RequestLogMessage message, Request okRequest) {
        Headers headers = okRequest.headers();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            message.addHeader(new LogHeaderMessage(name, value));
        }
    }

    protected void setLogBody(RequestLogMessage message, Request okRequest) {
        RequestBody requestBody = okRequest.body();
        LogBodyMessage logBodyMessage = new OkHttp3LogBodyMessage(requestBody);
        message.setBody(logBodyMessage);
    }


    public void logRequest(int retryCount,  Request okRequest) {
        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled() || !logConfiguration.isLogRequest()) {
            return;
        }
        RequestLogMessage requestLogMessage = buildRequestMessage(retryCount, okRequest);
        request.getLogHandler().logRequest(requestLogMessage);
    }

    public void logResponse(long startTime,  ForestResponse response) {
        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled()) {
            return;
        }
        long endTime = System.currentTimeMillis();
        ResponseLogMessage logMessage = new ResponseLogMessage(response, startTime, endTime, response.getStatusCode());
        LogHandler logHandler = request.getLogHandler();
        if (logHandler != null) {
            if (logConfiguration.isLogResponseStatus()) {
                logHandler.logResponseStatus(logMessage);
            }
            if (logConfiguration.isLogResponseContent() && response.isSuccess()) {
                logHandler.logResponseContent(logMessage);
            }
        }
    }

    protected AbstractOkHttp3Executor(ForestRequest request, OkHttp3ConnectionManager connectionManager, OkHttp3ResponseHandler okHttp3ResponseHandler) {
        this.request = request;
        this.connectionManager = connectionManager;
        this.okHttp3ResponseHandler = okHttp3ResponseHandler;
    }

    protected abstract BodyBuilder<Request.Builder> getBodyBuilder();

    protected abstract URLBuilder getURLBuilder();

    protected OkHttpClient getClient(ForestRequest request, LifeCycleHandler lifeCycleHandler) {
        return connectionManager.getClient(request, lifeCycleHandler);
    }

    protected void prepareMethod(Request.Builder builder) {
    }

    protected void prepareHeaders(Request.Builder builder) {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        String contentType = request.getContentType();
        String contentEncoding = request.getContentEncoding();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                String name = nameValue.getName();
                if (!"Content-Type".equalsIgnoreCase(name)
                        && !"Content-Encoding".equalsIgnoreCase(name)) {
                    builder.addHeader(name, MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
                }
            }
        }
        if (StringUtils.isNotEmpty(contentType) && !contentType.startsWith("multipart/form-data")) {
            builder.addHeader("Content-Type", contentType);
        }
        if (StringUtils.isNotEmpty(contentEncoding)) {
            builder.addHeader("Content-Encoding", contentEncoding);
        }
    }

    protected void prepareBody(Request.Builder builder, final LifeCycleHandler lifeCycleHandler) {
        getBodyBuilder().buildBody(builder, request, lifeCycleHandler);
    }

    public void execute(final LifeCycleHandler lifeCycleHandler, int retryCount) {
        OkHttpClient okHttpClient = getClient(request, lifeCycleHandler);
        URLBuilder urlBuilder = getURLBuilder();
        String url = urlBuilder.buildUrl(request);
        Request.Builder builder = new Request.Builder().url(url);
        prepareMethod(builder);
        prepareHeaders(builder);
        prepareBody(builder, lifeCycleHandler);

        final Request okRequest = builder.build();
        Call call = okHttpClient.newCall(okRequest);
        final OkHttp3ForestResponseFactory factory = new OkHttp3ForestResponseFactory();
        logRequest(0, okRequest);
        Date startDate = new Date();
        long startTime = startDate.getTime();
        if (request.isAsync()) {
            final OkHttp3ResponseFuture future = new OkHttp3ResponseFuture();
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ForestRetryException retryException = new ForestRetryException(
                            e, request, request.getRetryCount(), retryCount);
                    try {
                        request.getRetryer().canRetry(retryException);
                    } catch (Throwable throwable) {
                        future.failed(e);
                        ForestResponse response = factory.createResponse(request, null, lifeCycleHandler);
                        logResponse(startTime, response);
                        lifeCycleHandler.handleError(request, response, e);
                        return;
                    }
                    execute(lifeCycleHandler, retryCount + 1);
/*
                    future.failed(e);
                    ForestResponse response = factory.createResponse(request, null, lifeCycleHandler);
                    logResponse(startTime, response);
                    lifeCycleHandler.handleError(request, response, e);
*/
                }

                @Override
                public void onResponse(Call call, Response okResponse) throws IOException {
                    ForestResponse response = factory.createResponse(request, okResponse, lifeCycleHandler);
                    logResponse(startTime, response);
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
                        retryOrDoError(response, okResponse, future, lifeCycleHandler, retryCount, startTime);
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
                ForestRetryException retryException = new ForestRetryException(
                        e, request, request.getRetryCount(), retryCount);
                try {
                    request.getRetryer().canRetry(retryException);
                } catch (Throwable throwable) {
                    ForestResponse response = factory.createResponse(request, null, lifeCycleHandler);
                    logResponse(startTime, response);
                    lifeCycleHandler.handleSyncWitchException(request, response, e);
                    return;
                }
                execute(lifeCycleHandler, retryCount + 1);
                return;
            }
            ForestResponse response = factory.createResponse(request, okResponse, lifeCycleHandler);
            logResponse(startTime, response);
            if (response.isError()) {
                retryOrDoError(response, okResponse, null, lifeCycleHandler, retryCount, startTime);
                return;
            }
            okHttp3ResponseHandler.handleSync(okResponse, response);
        }
    }


    private void retryOrDoError(
            ForestResponse response, Response okResponse,
            OkHttp3ResponseFuture future, LifeCycleHandler lifeCycleHandler,
            int retryCount, long startTime) {
        ForestNetworkException networkException =
                new ForestNetworkException(okResponse.message(), okResponse.code(), response);
        ForestRetryException retryException = new ForestRetryException(
                networkException, request, request.getRetryCount(), retryCount);
        try {
            request.getRetryer().canRetry(retryException);
        } catch (Throwable throwable) {
            if (future != null) {
                future.failed(new ForestNetworkException(okResponse.message(), okResponse.code(), response));
            }
            logResponse(startTime, response);
            okHttp3ResponseHandler.handleSync(okResponse, response);
            return;
        }
        execute(lifeCycleHandler, retryCount + 1);
    }

    @Override
    public void execute(final LifeCycleHandler lifeCycleHandler) {
        execute(lifeCycleHandler, 0);
    }

    @Override
    public void close() {
    }



}
