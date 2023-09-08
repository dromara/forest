package org.dromara.forest.backend.httpclient.executor;

import org.dromara.forest.backend.AbstractHttpExecutor;
import org.dromara.forest.backend.BodyBuilder;
import org.dromara.forest.backend.ResponseHandler;
import org.dromara.forest.backend.SocksAuthenticator;
import org.dromara.forest.backend.httpclient.body.HttpclientBodyBuilder;
import org.dromara.forest.backend.httpclient.entity.HttpclientRequestWithBodyEntity;
import org.dromara.forest.backend.url.QueryableURLBuilder;
import org.dromara.forest.backend.url.URLBuilder;
import org.dromara.forest.http.ForestHeader;
import org.dromara.forest.http.ForestProxy;
import org.dromara.forest.http.ForestProxyType;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.http.ForestResponseFactory;
import org.dromara.forest.utils.RequestNameValue;
import org.dromara.forest.utils.StringUtils;
import org.dromara.forest.converter.json.ForestJsonConverter;
import org.dromara.forest.backend.httpclient.request.HttpclientRequestSender;
import org.dromara.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import org.dromara.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.dromara.forest.handler.LifeCycleHandler;
import org.dromara.forest.exceptions.ForestRuntimeException;
import org.dromara.forest.mapping.MappingTemplate;

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
        } catch (ForestRuntimeException e) {
            httpRequest.abort();
            throw e;
        } finally {
            if (hasProxyAuthenticator) {
                SocksAuthenticator.getInstance().removePasswordAuthenticator();
            }
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
