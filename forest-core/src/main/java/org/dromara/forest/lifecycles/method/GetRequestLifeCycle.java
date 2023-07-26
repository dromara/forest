package org.dromara.forest.lifecycles.method;

import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class GetRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        final MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType("GET");
        method.setMetaRequest(metaRequest);
    }


}
