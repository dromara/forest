package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class GetRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType("GET");
        method.setMetaRequest(metaRequest);
    }


}
