package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ResponseHandler;
import com.dtflys.forest.backend.httpclient.body.HttpclientBodyBuilder;
import com.dtflys.forest.backend.httpclient.entity.HttpclientRequestWithBodyEntity;
import com.dtflys.forest.backend.url.QueryableURLBuilder;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author gongjun
 * @since 2016-06-14
 */
public class HttpclientExecutor extends AbstractHttpExecutor {
    private final static URLBuilder URL_BUILDER = new QueryableURLBuilder();
    private final HttpclientResponseHandler httpclientResponseHandler;
    private HttpUriRequest httpRequest;
    private BodyBuilder<HttpUriRequest> bodyBuilder;

    protected HttpUriRequest buildRequest() {
        String url = buildUrl();
        return new HttpclientRequestWithBodyEntity(url, request.type().getName());
    }

    private URLBuilder getURLBuilder() {
        return URL_BUILDER;
    }

    protected String buildUrl() {
        return getURLBuilder().buildUrl(request, true);
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

    public HttpclientExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, requestSender);
        this.httpclientResponseHandler = httpclientResponseHandler;
    }

    public void prepareHeaders() {
        ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        List<RequestNameValue> headerList = request.getHeaderNameValueList();
        String contentType = request.getContentType();
        String contentEncoding = request.getContentEncoding();
        String contentTypeHeaderName = ForestHeader.CONTENT_TYPE;
        String contentEncodingHeaderName = ForestHeader.CONTENT_ENCODING;
        if (headerList != null && !headerList.isEmpty()) {
            for (RequestNameValue nameValue : headerList) {
                String name = nameValue.getName();
                if (ForestHeader.CONTENT_TYPE.equalsIgnoreCase(name)) {
                    contentTypeHeaderName = name;
                } else if (ForestHeader.CONTENT_ENCODING.equalsIgnoreCase(name)) {
                    contentEncodingHeaderName = name;
                } else {
                    httpRequest.setHeader(name, MappingTemplate.getParameterValue(jsonConverter, nameValue.getValue()));
                }
            }
        }
        if (StringUtils.isNotEmpty(contentType)) {
            httpRequest.setHeader(contentTypeHeaderName, contentType);
        }
        if (StringUtils.isNotEmpty(contentEncoding)) {
            httpRequest.setHeader(contentEncodingHeaderName, contentEncoding);
        }
    }

    public void prepareBody(LifeCycleHandler lifeCycleHandler) {
        bodyBuilder.buildBody(httpRequest, request, lifeCycleHandler);
    }


    @Override
    public void execute(LifeCycleHandler lifeCycleHandler) {
        prepare(lifeCycleHandler);
        Date startDate = new Date();
        ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        try {
            request.pool().awaitRequest(request);
            requestSender.sendRequest(
                    request,
                    this,
                    httpclientResponseHandler,
                    httpRequest,
                    lifeCycleHandler,
                    startDate);
        } catch (IOException e) {
            httpRequest.abort();
            response = forestResponseFactory.createResponse(request, null, lifeCycleHandler, e, startDate);
            lifeCycleHandler.handleSyncWithException(request, response, e);
            return;
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        } finally {
            request.pool().finish(request);
        }
    }

    @Override
    public ResponseHandler getResponseHandler() {
        return httpclientResponseHandler;
    }

    @Override
    public ForestResponseFactory getResponseFactory() {
        return new HttpclientForestResponseFactory();
    }

    @Override
    public void close() {
        if (httpRequest != null && !httpRequest.isAborted()) {
            httpRequest.abort();
        }
    }


}
