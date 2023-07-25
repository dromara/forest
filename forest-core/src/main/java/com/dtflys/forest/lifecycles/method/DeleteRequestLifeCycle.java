package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class DeleteRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        final MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType(ForestRequestType.DELETE.getName());
        method.setMetaRequest(metaRequest);
    }


}
