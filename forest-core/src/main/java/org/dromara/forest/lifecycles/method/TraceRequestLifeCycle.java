package org.dromara.forest.lifecycles.method;

import org.dromara.forest.http.ForestRequestType;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class TraceRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType(ForestRequestType.TRACE.getName());
        method.setMetaRequest(metaRequest);
    }


}
