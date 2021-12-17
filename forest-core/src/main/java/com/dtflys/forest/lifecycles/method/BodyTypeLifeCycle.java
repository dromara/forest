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

public class BodyTypeLifeCycle implements MethodAnnotationLifeCycle<BodyType, Object> {

    @Override
    public void onMethodInitialized(ForestMethod method, BodyType annotation) {
        Map<String, Object> attrs = ReflectUtils.getAttributesFromAnnotation(annotation);
        String type = (String) attrs.get("type");
        Object encodeClass = attrs.get("encoder");
        MetaRequest metaRequest = method.getMetaRequest();
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
