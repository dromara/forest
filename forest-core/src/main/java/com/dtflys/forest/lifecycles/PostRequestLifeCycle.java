package com.dtflys.forest.lifecycles;

import com.dtflys.forest.http.ForestRequestType;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;

import java.lang.annotation.Annotation;


public class PostRequestLifeCycle extends RequestLifeCycle {

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        MetaRequest metaRequest = createMetaRequest(annotation);
        metaRequest.setType(ForestRequestType.POST.getName());
        method.setMetaRequest(metaRequest);
    }


}
