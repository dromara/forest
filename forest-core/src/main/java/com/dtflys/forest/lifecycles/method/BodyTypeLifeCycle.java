package com.dtflys.forest.lifecycles.method;

import com.dtflys.forest.annotation.BodyType;
import com.dtflys.forest.converter.ForestEncoder;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.lifecycles.MethodAnnotationLifeCycle;
import com.dtflys.forest.reflection.ForestMethod;
import com.dtflys.forest.reflection.MetaRequest;
import com.dtflys.forest.utils.ReflectUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

public class BodyTypeLifeCycle implements MethodAnnotationLifeCycle<BodyType, Void> {

    @Override
    public void onMethodInitialized(ForestMethod method, BodyType annotation) {
        final Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        final String type = (String) attrs.get("type");
        final Object encodeClass = attrs.get("encoder");
        final MetaRequest metaRequest = method.getMetaRequest();
        if (metaRequest == null) {
            return;
        }
        metaRequest.setBodyType(type);
        if (encodeClass != null
                && encodeClass instanceof Class
                && !((Class<?>) encodeClass).isInterface()
                && ForestEncoder.class.isAssignableFrom((Class<?>) encodeClass)) {
            metaRequest.setEncoder((Class<? extends ForestEncoder>) encodeClass);
        }
    }


}
