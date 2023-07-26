package org.dromara.forest.lifecycles.method;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.utils.ReflectUtils;

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
