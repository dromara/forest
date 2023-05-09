package org.dromara.forest.lifecycles.method;

import org.dromara.forest.annotation.Headers;
import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;


public class HeadersLifeCycle implements MethodAnnotationLifeCycle<Headers, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, Headers annotation) {
        MetaRequest metaRequest = method.getMetaRequest();
        String[] headers = annotation.value();
        String[] oldHeaders = metaRequest.getHeaders();
        int len = headers.length + oldHeaders.length;
        String[] newHeaders = new String[headers.length + oldHeaders.length];
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
    public boolean beforeExecute(ForestRequest request) {
        return true;
    }

}
