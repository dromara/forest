package com.dtflys.forest.lifecycles;

import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.reflection.MetaLifeCycle;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.annotation.Annotation;


public class RequestLifeCycle implements MetaLifeCycle<Annotation, Object> {

    @Override
    public MetaRequest buildMetaRequest(Annotation annotation) {
        MetaRequest metaRequest = new MetaRequest(annotation);
        ReflectUtils.copyAnnotationAttributes(annotation, metaRequest);
        return metaRequest;
    }


}
