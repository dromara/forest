package org.dromara.forest.lifecycles.method;

import org.dromara.forest.http.ForestRequestType;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class DeleteRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        final MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType(ForestRequestType.DELETE.getName());
        method.setMetaRequest(metaRequest);
    }


}
