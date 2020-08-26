package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.body.NoneBodyBuilder;
import com.dtflys.forest.backend.httpclient.HttpclientRequestProvider;
import com.dtflys.forest.backend.httpclient.body.HttpclientBodyBuilder;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;


import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author gongjun
 * @since 2016-06-14
 */
public abstract class AbstractHttpclientExecutor<T extends  HttpRequestBase> extends AbstractHttpExecutor {
    private static Logger log = LoggerFactory.getLogger(AbstractHttpclientExecutor.class);

    protected final HttpclientResponseHandler httpclientResponseHandler;
    protected String url;
    protected final String typeName;
    protected T httpRequest;
    protected BodyBuilder<T> bodyBuilder;


    protected T buildRequest() {
        url = buildUrl();
        return getRequestProvider().getRequest(url);
    }

    protected abstract HttpclientRequestProvider<T> getRequestProvider();

    protected abstract URLBuilder getURLBuilder();


    protected String buildUrl() {
        return getURLBuilder().buildUrl(request);
    }


    protected void prepareBodyBuilder() {
        bodyBuilder = new HttpclientBodyBuilder();
    }

    protected void prepare(LifeCycleHandler lifeCycleHandler) {
        httpRequest = buildRequest();
        prepareBodyBuilder();
        prepareHeaders();
        prepareBody(lifeCycleHandler);
    }

    public AbstractHttpclientExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, requestSender);
        this.typeName = request.getType().getName();
        this.httpclientResponseHandler = httpclientResponseHandler;
    }

    public void prepareHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        String contentType = request.getContentType();
        String contentEncoding = request.getContentEncoding();
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                String name = nameValue.getName();
                if (!name.equalsIgnoreCase("Content-Type")
                        && !name.equalsIgnoreCase("Content-Encoding")) {
                    httpRequest.setHeader(name, MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
                }
            }
        }
        if (StringUtils.isNotEmpty(contentType) && !contentType.startsWith("multipart/form-data")) {
            httpRequest.setHeader("Content-Type", contentType);
        }
        if (StringUtils.isNotEmpty(contentEncoding)) {
            httpRequest.setHeader("Content-Encoding", contentEncoding);
        }
    }

    public void prepareBody(LifeCycleHandler lifeCycleHandler) {
        bodyBuilder.buildBody(httpRequest, request, lifeCycleHandler);
    }


    protected static void logContent(String content) {
        log.info("[Forest] " + content);
    }

    public void logRequest(int retryCount, T httpReq) {
        if (!request.isLogEnable()) return;
        String requestLine = getLogContentForRequestLine(retryCount, httpReq);
        String headers = getLogContentForHeaders(httpReq);
        String body = getLogContentForBody(httpReq);
        String content = "Request: \n\t" + requestLine;
        if (StringUtils.isNotEmpty(headers)) {
            content += "\n\tHeaders: \n" + headers;
        }
        if (StringUtils.isNotEmpty(body)) {
            content += "\n\tBody: " + body;
        }
        logContent(content);
    }

    public void logResponse(long startTime, ForestResponse response) {
        if (!request.isLogEnable()) return;
        long endTime = new Date().getTime();
        long time = endTime - startTime;
        logContent("Response: Status = " + response.getStatusCode() + ", Time = " + time + "ms");
    }

    protected String getLogContentForRequestLine(int retryCount, T httpReq) {
        if (retryCount == 0) {
            return httpReq.getRequestLine().toString();
        }
        else {
            return "[Retry: " + retryCount + "] " + httpReq.getRequestLine().toString();
        }
    }

    protected String getLogContentForHeaders(T httpReq) {
        StringBuffer buffer = new StringBuffer();
        Header[] headers = httpReq.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            buffer.append("\t\t" + header.getName() + ": " + header.getValue());
            if (i < headers.length - 1) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    protected String getLogContentForBody(T httpReq) {
        if (!(httpReq instanceof HttpEntityEnclosingRequestBase)) {
            return null;
        }
        HttpEntityEnclosingRequestBase entityEnclosingRequest = (HttpEntityEnclosingRequestBase) httpReq;
        HttpEntity entity = entityEnclosingRequest.getEntity();
        if (entity.getContentType().getValue().startsWith("multipart/")) {
            Class[] paramTypes = new Class[0];
            Object[] args = new Object[0];
            List<FormBodyPart> parts = null;
            try {
                Method getMultipartMethod = entity.getClass().getDeclaredMethod("getMultipart", paramTypes);
                getMultipartMethod.setAccessible(true);
                Object multipart = getMultipartMethod.invoke(entity, args);
                if (multipart != null) {
                    Method getBodyPartsMethod = multipart.getClass().getDeclaredMethod("getBodyParts", paramTypes);
                    getBodyPartsMethod.setAccessible(true);
                    parts = (List<FormBodyPart>) getBodyPartsMethod.invoke(multipart, args);
                }
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            Long contentLength = null;
            try {
                contentLength = entity.getContentLength();
            } catch (Throwable th) {
            }

            if (parts == null) {
                String result = "[" + entity.getContentType().getValue();
                if (contentLength != null) {
                    result += "; length=" + contentLength;
                }
                return result + "]";
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("[")
                        .append(entity.getContentType().getValue());
                if (contentLength != null) {
                    builder.append("; length=").append(contentLength);
                }
                builder.append("] parts:");
                for (FormBodyPart part : parts) {
                    ContentBody partBody = part.getBody();
                    MinimalField disposition = part.getHeader().getField("Content-Disposition");
                    builder.append("\n             -- [")
                            .append(disposition.getBody());
                    if (partBody instanceof StringBody) {
                        Reader reader = ((StringBody) partBody).getReader();
                        BufferedReader bufferedReader = new BufferedReader(reader);
                        String value = null;
                        try {
                            value = getLogContentFormBufferedReader(bufferedReader);
                        } catch (IOException e) {
                        }
                        builder.append("; value=\"")
                                .append(value)
                                .append("\"]");
                    } else {
                        Long length = null;
                        length = partBody.getContentLength();
                        if (length != null) {
                            builder.append("; length=").append(length);
                        }
                        builder.append("]");
                    }
                }
                return builder.toString();
            }
        }
        return getLogContentForStringBody(entity);
    }


    private String getLogContentForStringBody(HttpEntity entity) {
        InputStream in = null;
        try {
            in = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return getLogContentFormBufferedReader(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLogContentFormBufferedReader(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        String body;
        List<String> lines = new LinkedList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        for (int i = 0, len = lines.size(); i < len; i++) {
            builder.append(lines.get(i));
            if (i < len - 1) {
                builder.append("\\n");
            }
        }
        body = builder.toString();
        return body;
    }

    public void execute(LifeCycleHandler lifeCycleHandler) {
        prepare(lifeCycleHandler);
        execute(0, lifeCycleHandler);
    }


    public void execute(int retryCount, LifeCycleHandler lifeCycleHandler) {
        Date startDate = new Date();
        long startTime = startDate.getTime();
        try {
            logRequest(retryCount, httpRequest);
            requestSender.sendRequest(request, httpclientResponseHandler, httpRequest, lifeCycleHandler, startTime, 0);
        } catch (IOException e) {
            if (retryCount >= request.getRetryCount()) {
                httpRequest.abort();
                ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
                response = forestResponseFactory.createResponse(request, null, lifeCycleHandler);
                logResponse(startTime, response);
                lifeCycleHandler.handleSyncWitchException(request, response, e);
//                throw new ForestRuntimeException(e);
                return;
            }
            log.error(e.getMessage());
//            execute(retryCount + 1, lifeCycleHandler);
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        }
    }

    public void close() {
/*
        if (httpResponse != null) {
            try {
                EntityUtils.consume(httpResponse.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client0.getConnectionManager().closeExpiredConnections();
*/
    }


}
