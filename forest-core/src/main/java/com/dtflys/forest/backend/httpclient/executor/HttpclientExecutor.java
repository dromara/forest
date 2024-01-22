package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.BodyBuilder;
import com.dtflys.forest.backend.ResponseHandler;
import com.dtflys.forest.backend.SocksAuthenticator;
import com.dtflys.forest.backend.httpclient.body.HttpclientBodyBuilder;
import com.dtflys.forest.backend.httpclient.entity.HttpclientRequestWithBodyEntity;
import com.dtflys.forest.backend.url.QueryableURLBuilder;
import com.dtflys.forest.backend.url.URLBuilder;
import com.dtflys.forest.http.ForestHeader;
import com.dtflys.forest.http.ForestProxy;
import com.dtflys.forest.http.ForestProxyType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.pool.ForestRequestPool;
import com.dtflys.forest.utils.RequestNameValue;
import com.dtflys.forest.utils.StringUtils;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.mapping.MappingTemplate;

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
        return getURLBuilder().buildUrl(request);
    }

    protected void prepareBodyBuilder() {
        bodyBuilder = new HttpclientBodyBuilder();
    }

    protected void prepare(LifeCycleHandler lifeCycleHandler) {
        httpRequest = buildRequest();
        prepareBodyBuilder();
        prepareBody(lifeCycleHandler);
        prepareHeaders();
    }

    public HttpclientExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, requestSender);
        this.httpclientResponseHandler = httpclientResponseHandler;
    }

    public void prepareHeaders() {
        final ForestJsonConverter jsonConverter = request.getConfiguration().getJsonConverter();
        final List<RequestNameValue> headerList = request.getHeaderNameValueList();
        final String contentType = request.getContentType();
        final String contentEncoding = request.getContentEncoding();
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
        final Date startDate = new Date();
        final ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        final ForestProxy proxy = request.getProxy();
        boolean hasProxyAuthenticator = false;
        if (proxy != null && proxy.getType() == ForestProxyType.SOCKS) {
            final String username = proxy.getUsername();
            final String password = proxy.getPassword();
            if (StringUtils.isNotEmpty(username)) {
                SocksAuthenticator.getInstance().setPasswordAuthenticator(username, password);
                hasProxyAuthenticator = true;
            }
        }
        final ForestRequestPool pool = request.pool();
        try {
            pool.awaitRequest(request);
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
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        } finally {
            if (hasProxyAuthenticator) {
                SocksAuthenticator.getInstance().removePasswordAuthenticator();
            }
            pool.finish(request);
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
