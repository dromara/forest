package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.Headers;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.interceptor.ForestJointPoint;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;


public class HeadersLifeCycle implements MethodAnnotationLifeCycle<Headers, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, Headers annotation) {
        final MetaRequest metaRequest = method.getMetaRequest();
        final String[] headers = annotation.value();
        final String[] oldHeaders = metaRequest.getHeaders();
//        int len = headers.length + oldHeaders.length;
        final String[] newHeaders = new String[headers.length + oldHeaders.length];
        for (int i = 0; i < oldHeaders.length; i++) {
            newHeaders[i] = oldHeaders[i];
        }
        for (int i = 0; i < headers.length; i++) {
            newHeaders[oldHeaders.length + i] = headers[i];
        }
//        String[] newHeaders = ArrayUtils.addAll(oldHeaders, headers);
        metaRequest.setHeaders(newHeaders);
    }

    @Override
    public ForestJointPoint beforeExecute(ForestRequest request) {
        return proceed();
    }

}
