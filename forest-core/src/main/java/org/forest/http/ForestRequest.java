/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jun Gong
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.forest.http;

import org.forest.callback.OnError;
import org.forest.callback.OnSuccess;
import org.forest.config.ForestConfiguration;
import org.forest.exceptions.ForestRuntimeException;
import org.forest.executors.ForestExecutorFactory;
import org.forest.executors.HttpExecutor;
import org.forest.handler.ResponseHandler;
import org.forest.interceptor.Interceptor;
import org.forest.interceptor.InterceptorChain;
import org.forest.ssl.SSLKeyStore;
import org.forest.utils.ForestDataType;
import org.forest.utils.RequestNameValue;

import java.io.InputStream;
import java.util.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2016-03-24
 */
public class ForestRequest<T> {

    private final ForestConfiguration configuration;

    private String protocol;

    private String url;

    private String query;

    private String type;

    private String encode;

    private String responseEncode = "UTF-8";

    private boolean async;

    private ForestDataType dataType;

    private String contentType;

    private int timeout = 3000;

    private int retryCount = 0;

    private Map<String, Object> data = new LinkedHashMap<String, Object>();

    private Map<String, Object> headers = new LinkedHashMap<>();

    private Object[] arguments;

    private String requestBody;

    private InputStream certificateInputStream;

    private OnSuccess onSuccess;

    private OnError onError;

    private InterceptorChain interceptorChain = new InterceptorChain();

    private boolean logEnable = true;

    private SSLKeyStore keyStore;

    public ForestRequest(ForestConfiguration configuration) {
        this.configuration = configuration;
    }

    public ForestConfiguration getConfiguration() {
        return configuration;
    }

    public String getProtocol() {
        return protocol;
    }

    public ForestRequest setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ForestRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public ForestRequest setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getType() {
        return type;
    }

    public ForestRequest setType(String type) {
        this.type = type;
        return this;
    }

    public String getEncode() {
        return encode;
    }

    public ForestRequest setEncode(String encode) {
        this.encode = encode;
        return this;
    }

    public String getResponseEncode() {
        return responseEncode;
    }

    public ForestRequest<T> setResponseEncode(String responseEncode) {
        this.responseEncode = responseEncode;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public ForestRequest setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public ForestDataType getDataType() {
        return dataType;
    }

    public ForestRequest setDataType(ForestDataType dataType) {
        this.dataType = dataType;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public ForestRequest setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public ForestRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public ForestRequest setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }


    public ForestRequest addData(String name, Object value) {
        this.data.put(name, value);
        return this;
    }


    public ForestRequest addData(RequestNameValue nameValue) {
        this.data.put(nameValue.getName(), nameValue.getValue());
        return this;
    }

    public ForestRequest addData(List<RequestNameValue> data) {
        putMapAddList(this.data, data);
        return this;
    }

    public List<RequestNameValue> getDataNameValueList() {
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        for (Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Object value = entry.getValue();
            RequestNameValue nameValue = new RequestNameValue(name, value, false);
            nameValueList.add(nameValue);
        }
        return nameValueList;
    }

    public List<RequestNameValue> getHeaderNameValueList() {
        List<RequestNameValue> nameValueList = new ArrayList<RequestNameValue>();
        for (Iterator<Map.Entry<String, Object>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            RequestNameValue nameValue = new RequestNameValue(entry.getKey(), String.valueOf(entry.getValue()), false);
            nameValueList.add(nameValue);
        }
        return nameValueList;
    }

    public ForestRequest<T> setArguments(Object[] arguments) {
        this.arguments = arguments;
        return this;
    }

    public Object getArgument(int index) {
        return arguments[index];
    }


    public Map<String, Object> getHeaders() {
        return headers;
    }

    public ForestRequest addHeader(String name, Object value) {
        this.headers.put(name, value);
        return this;
    }

    public ForestRequest addHeader(RequestNameValue nameValue) {
        this.headers.put(nameValue.getName(), nameValue.getValue());
        return this;
    }

    public ForestRequest addHeaders(List<RequestNameValue> headers) {
        putMapAddList(this.headers, headers);
        return this;
    }

    private void putMapAddList(Map<String, Object> map, List<RequestNameValue> source) {
        for (int i = 0; i < source.size(); i++) {
            RequestNameValue nameValue = source.get(i);
            map.put(nameValue.getName(), nameValue.getValue());
        }
    }

    public String getRequestBody() {
        return requestBody;
    }

    public ForestRequest setRequestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public InputStream getCertificateInputStream() {
        return certificateInputStream;
    }

    public void setCertificateInputStream(InputStream certificateInputStream) {
        this.certificateInputStream = certificateInputStream;
    }

    public OnSuccess getOnSuccess() {
        return onSuccess;
    }

    public ForestRequest setOnSuccess(OnSuccess onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public OnError getOnError() {
        return onError;
    }

    public ForestRequest setOnError(OnError onError) {
        this.onError = onError;
        return this;
    }

    public ForestRequest<T> addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
        return this;
    }

    public InterceptorChain getInterceptorChain() {
        return interceptorChain;
    }


    public boolean isLogEnable() {
        return logEnable;
    }

    public ForestRequest setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
        return this;
    }

    public SSLKeyStore getKeyStore() {
        return keyStore;
    }

    public ForestRequest setKeyStore(SSLKeyStore keyStore) {
        this.keyStore = keyStore;
        return this;
    }

    public void execute(ForestExecutorFactory executorFactory, ResponseHandler responseHandler) {
        HttpExecutor executor  = executorFactory.create(this, responseHandler);
        if (executor != null) {
            if (interceptorChain.beforeExecute(this)) {
                try {
                    executor.execute(responseHandler);
                } catch (ForestRuntimeException e) {
                    throw e;
//                if (onError == null) {
//                    throw e;
//                }
//                ForestRuntimeException runtimeException = e;
//                if (!(e instanceof ForestRuntimeException)) {
//                    runtimeException = new ForestRuntimeException(e);
//                }
//                onError.onError(runtimeException, this);
//                interceptorChain.onError(e, this, response);
                } finally {
                    executor.close();
//                interceptorChain.afterExecute(this, response);
                }
            }
        }
    }

}
