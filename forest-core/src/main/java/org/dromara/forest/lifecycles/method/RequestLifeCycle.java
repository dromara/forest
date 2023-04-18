package org.dromara.forest.lifecycles.method;

import org.dromara.forest.http.ForestRequest;
import org.dromara.forest.reflection.ForestMethod;
import org.dromara.forest.reflection.MetaRequest;
import org.dromara.forest.lifecycles.MethodAnnotationLifeCycle;
import org.dromara.forest.utils.ReflectUtil;

import java.lang.annotation.Annotation;


public class RequestLifeCycle implements MethodAnnotationLifeCycle<Annotation, Object> {

    protected MetaRequest createMetaRequest(Annotation annotation) {
        MetaRequest metaRequest = new MetaRequest(annotation);
        ReflectUtil.copyAnnotationAttributes(annotation, metaRequest);
        return metaRequest;
    }

    @Override
    public void onMethodInitialized(ForestMethod method, Annotation annotation) {
        MetaRequest metaRequest = createMetaRequest(annotation);
        method.setMetaRequest(metaRequest);
    }

    @Override
    public boolean beforeExecute(ForestRequest request) {
        return MethodAnnotationLifeCycle.super.beforeExecute(request);
    }
}
