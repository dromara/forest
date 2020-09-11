package com.dtflys.forest.backend.httpclient.executor;

import com.dtflys.forest.backend.httpclient.body.HttpclientBodyBuilder;
import com.dtflys.forest.http.ForestRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import com.dtflys.forest.backend.httpclient.request.HttpclientRequestSender;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2017-04-20 13:22
 */
public abstract class AbstractHttpclientEntityExecutor<T extends HttpEntityEnclosingRequestBase> extends AbstractHttpclientExecutor<T> {

    public AbstractHttpclientEntityExecutor(ForestRequest request, HttpclientResponseHandler httpclientResponseHandler, HttpclientRequestSender requestSender) {
        super(request, httpclientResponseHandler, requestSender);
    }

    @Override
    protected void prepareBodyBuilder() {
        bodyBuilder = new HttpclientBodyBuilder<>();
    }


}
