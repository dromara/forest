package com.dtflys.forest.lifecycles;

import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.reflection.MetaRequestLifeCycle;


public class RequestLifeCycle implements MetaRequestLifeCycle<Request, Object> {

    @Override
    public MetaRequest buildMetaRequest(Request annotation) {
        MetaRequest metaRequest = new MetaRequest();
        metaRequest.setUrl(annotation.url());
        metaRequest.setType(annotation.type());
        metaRequest.setDataType(annotation.dataType());
        metaRequest.setContentType(annotation.contentType());
        metaRequest.setKeyStore(annotation.keyStore());
        metaRequest.setContentEncoding(annotation.contentEncoding());
        metaRequest.setCharset(annotation.charset());
        metaRequest.setProgressStep(annotation.progressStep());
        metaRequest.setAsync(annotation.async());
        metaRequest.setRetryer(annotation.retryer());
        metaRequest.setDecoder(annotation.decoder());
        metaRequest.setData(annotation.data());
        metaRequest.setTimeout(annotation.timeout());
        metaRequest.setRetryCount(annotation.retryCount());
        metaRequest.setMaxRetryInterval(annotation.maxRetryInterval());
        metaRequest.setLogEnabled(annotation.logEnabled());
        metaRequest.setInterceptor(annotation.interceptor());
        return metaRequest;
    }
}
