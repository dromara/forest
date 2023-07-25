package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.annotation.Annotation;


public class RequestLifeCycle implements MethodAnnotationLifeCycle<Annotation, Object> {

    protected MetaRequest createMetaRequest(Annotation annotation) {
        final MetaRequest metaRequest = new MetaRequest(annotation);
        ReflectUtils.copyAnnotationAttributes(annotation, metaRequest);
        return metaRequest;
    }

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        final MetaRequest metaRequest = createMetaRequest(annotation);
        method.setMetaRequest(metaRequest);
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        return MethodAnnotationLifeCycle.super.beforeExecute(request);
    }
}
