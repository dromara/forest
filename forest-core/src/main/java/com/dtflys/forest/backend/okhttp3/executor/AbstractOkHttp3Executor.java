package com.dtflys.forest.backend.okhttp3.executor;

import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.HttpExecutor;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.*;
import com.dtflys.forest.backend.okhttp3.conn.OkHttp3ConnectionManager;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ForestResponseFactory;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseFuture;
import com.dtflys.forest.backend.okhttp3.response.OkHttp3ResponseHandler;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.handler.ResponseHandler;
import com.dtflys.forest.mapping.MappingTemplate;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Date;
import java.util.List;


/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2018-02-27 17:55
 */
public abstract class AbstractOkHttp3Executor implements HttpExecutor {

    private final static Log log = LogFactory.getLog(AbstractOkHttp3Executor.class);

    protected final ForestRequest request;

    private final OkHttp3ConnectionManager connectionManager;

    private final OkHttp3ResponseHandler okHttp3ResponseHandler;

    protected String getLogContentForRequestLine(int retryCount, Request okRequest) {
        HttpUrl url = okRequest.url();
        String scheme = url.scheme().toUpperCase();
        String uri = url.uri().toString();
        String method = okRequest.method();
        String httpline = method + " " + uri + " " + scheme;
        if (retryCount == 0) {
            return httpline;
        }
        else {
            return "[Retry: " + retryCount + "] " + httpline;
        }
    }

    protected String getLogContentForHeaders(Request okRequest) {
        StringBuffer buffer = new StringBuffer();
        Headers headers = okRequest.headers();
        for (int i = 0; i < headers.size(); i++) {
            String name = headers.name(i);
            String value = headers.value(i);
            buffer.append("\t\t" + name + ": " + value);
            if (i < headers.size() - 1) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }


    protected String getLogContentForBody(Request okRequest) {
        RequestBody requestBody = okRequest.body();
        if (requestBody == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Sink sink = Okio.sink(out);
        BufferedSink bufferedSink = Okio.buffer(sink);
        try {
            requestBody.writeTo(bufferedSink);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedSink.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line;
        String body;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line + " ");
            }
            body = buffer.toString();
            return body;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }



    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }

    public void logRequest(int retryCount,  Request okRequest) {
        if (!request.isLogEnable()) return;
        okRequest.url().uri().toString();
        String requestLine = getLogContentForRequestLine(retryCount, okRequest);
        String headers = getLogContentForHeaders(okRequest);
        String body = getLogContentForBody(okRequest);
        String content = "Request: \n\t" + requestLine;
        if (StringUtils.isNotEmpty(headers)) {
            content += "\n\tHeaders: \n" + headers;
        }
        if (StringUtils.isNotEmpty(body)) {
            content += "\n\tBody: " + body;
        }
        logContent(content);
    }

    public void logResponse(long startTime,  ForestResponse response) {
        if (!request.isLogEnable()) return;
        long endTime = new Date().getTime();
        long time = endTime - startTime;
        logContent("Response: Status = " + response.getStatusCode() + ", Time = " + time + "ms");
    }

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
        String encode = request.getEncode();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                builder.addHeader(nameValue.getName(), MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
            }
        }
        if (StringUtils.isNotEmpty(encode)) {
            builder.addHeader("Content-Encoding", encode);
        }
    }

    protected void prepareBody(Request.Builder builder) {
        getBodyBuilder().buildBody(builder, request);
    }

    protected void requestMethod(Request.Builder builder) {
        prepareBody(builder);
    }

    public void execute(final ResponseHandler responseHandler, int retryCount) {
        OkHttpClient okHttpClient = getClient(request);
        URLBuilder urlBuilder = getURLBuilder();
        String url = urlBuilder.buildUrl(request);
        Request.Builder builder = new Request.Builder().url(url);
        prepareHeaders(builder);
        requestMethod(builder);

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
                    future.failed(e);
                    ForestResponse response = factory.createResponse(request, null);
                    logResponse(startTime, response);
                    responseHandler.handleError(request, response, e);
                }

                @Override
                public void onResponse(Call call, Response okResponse) throws IOException {
                    ForestResponse response = factory.createResponse(request, okResponse);
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
                        if (retryCount > request.getRetryCount()) {
                            future.failed(new ForestNetworkException(okResponse.message(), okResponse.code(), response));
                            okHttp3ResponseHandler.handleError(response);
                            return;
                        }
                        execute(responseHandler, retryCount + 1);
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
                if (retryCount >= request.getRetryCount()) {
                    ForestResponse response = factory.createResponse(request, okResponse);
                    logResponse(startTime, response);
                    responseHandler.handleError(request, response, e);
                    return;
                }
                execute(responseHandler, retryCount + 1);
            }
            ForestResponse response = factory.createResponse(request, okResponse);
            okHttp3ResponseHandler.handleSync(okResponse, response);
        }
    }


    @Override
    public void execute(final ResponseHandler responseHandler) {
        execute(responseHandler, 0);
    }

    @Override
    public void close() {
    }



}
